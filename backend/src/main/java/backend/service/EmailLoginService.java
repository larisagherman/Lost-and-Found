package backend.service;

import backend.model.EmailLoginToken;
import backend.model.Users;
import backend.repository.EmailLoginTokenRepository;
import backend.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.HexFormat;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class EmailLoginService {
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private final UsersRepository usersRepository;
    private final EmailLoginTokenRepository tokenRepository;
    private final JavaMailSender mailSender;

    @Value("${app.auth.token-ttl-minutes:10}")
    private long tokenTtlMinutes;

    @Value("${app.mail.from:no-reply@lostfound.local}")
    private String fromAddress;

    @Transactional
    public void sendLoginToken(String submittedEmail) {
        String normalizedEmail = submittedEmail.trim().toLowerCase(Locale.ROOT);
        Users user = usersRepository.findByEmailIgnoreCase(normalizedEmail).orElse(null);

        // Only preloaded/approved users can sign in. Do not create accounts here.
        if (user == null) {
            return;
        }

        byte[] randomBytes = new byte[32];
        SECURE_RANDOM.nextBytes(randomBytes);
        String rawToken = Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);

        Instant now = Instant.now();
        tokenRepository.findAllByUser_IdAndUsedAtIsNull(user.getId())
                .forEach(existingToken -> existingToken.setUsedAt(now));

        EmailLoginToken token = new EmailLoginToken();
        token.setUser(user);
        token.setTokenHash(hashToken(rawToken));
        token.setExpiresAt(now.plus(tokenTtlMinutes, ChronoUnit.MINUTES));
        tokenRepository.save(token);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromAddress);
        message.setTo(user.getEmail());
        message.setSubject("Your Lost and Found sign-in code");
        message.setText("Your one-time sign-in code is:\n\n" + rawToken
                + "\n\nIt expires in " + tokenTtlMinutes
                + " minutes and can be used once. If you did not request it, ignore this email.");
        mailSender.send(message);
    }

    @Transactional
    public Users consumeLoginToken(String rawToken) {
        EmailLoginToken token = tokenRepository.findByTokenHashForUpdate(hashToken(rawToken))
                .orElseThrow(() -> new InvalidLoginTokenException("Invalid or expired sign-in code."));

        Instant now = Instant.now();
        if (token.getUsedAt() != null || !token.getExpiresAt().isAfter(now)) {
            throw new InvalidLoginTokenException("Invalid or expired sign-in code.");
        }

        token.setUsedAt(now);
        return token.getUser();
    }

    private String hashToken(String token) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256")
                    .digest(token.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(digest);
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 is not available.", exception);
        }
    }

    public static class InvalidLoginTokenException extends RuntimeException {
        public InvalidLoginTokenException(String message) {
            super(message);
        }
    }
}
