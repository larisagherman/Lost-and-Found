package backend.service;

import backend.model.ClaimRequest;
import backend.model.UserRole;
import backend.model.Users;
import backend.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.LinkedHashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class ClaimNotificationService {
    private final JavaMailSender mailSender;
    private final UsersRepository usersRepository;

    @Value("${app.mail.from:no-reply@lostfound.local}")
    private String fromAddress;

    public void notifySubmitted(ClaimRequest claim) {
        String subject = "A claim was submitted for a found item";
        String text = "A claim was submitted for '" + claim.getFoundItemReport().getItemName()
                + "'. An administrator at your institution will review it.";
        notifyPeople(claim, subject, text);
    }

    public void notifyStatusChanged(ClaimRequest claim, String message) {
        notifyPeople(claim, "Update about your campus lost-and-found claim", message);
    }

    private void notifyPeople(ClaimRequest claim, String subject, String text) {
        Set<String> recipients = new LinkedHashSet<>();
        recipients.add(claim.getClaimant().getEmail());
        recipients.add(claim.getFoundItemReport().getReporter().getEmail());
        usersRepository.findByInstitution_IdAndRole(
                        claim.getFoundItemReport().getReporter().getInstitution().getId(), UserRole.ADMIN)
                .forEach(admin -> recipients.add(admin.getEmail()));

        for (String recipient : recipients) {
            try {
                SimpleMailMessage mail = new SimpleMailMessage();
                mail.setFrom(fromAddress);
                mail.setTo(recipient);
                mail.setSubject(subject);
                mail.setText(text + "\n\nItem: " + claim.getFoundItemReport().getItemName()
                        + "\nClaim status: " + claim.getStatus());
                mailSender.send(mail);
            } catch (MailException exception) {
                // The claim is saved even if the local mail service is unavailable.
                log.warn("Could not send claim notification to {}: {}", recipient, exception.getMessage());
            }
        }
    }
}
