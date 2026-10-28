package backend.controller;

import backend.dto.AuthUserResponse;
import backend.dto.EmailLoginRequest;
import backend.dto.VerifyEmailTokenRequest;
import backend.model.Users;
import backend.security.AppUserPrincipal;
import backend.service.EmailLoginService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final EmailLoginService emailLoginService;
    private final SecurityContextRepository securityContextRepository;

    @GetMapping("/csrf")
    public Map<String, String> csrfToken(@RequestAttribute("_csrf") CsrfToken csrfToken) {
        return Map.of("headerName", csrfToken.getHeaderName(), "token", csrfToken.getToken());
    }

    @PostMapping("/email-link")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Map<String, String> requestEmailLink(@Valid @RequestBody EmailLoginRequest request) {
        emailLoginService.sendLoginToken(request.getEmail());
        return Map.of("message", "If that address is registered, a sign-in code has been sent.");
    }

    @PostMapping("/verify")
    public AuthUserResponse verifyEmailToken(
            @Valid @RequestBody VerifyEmailTokenRequest requestBody,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        Users user = emailLoginService.consumeLoginToken(requestBody.getToken());
        AppUserPrincipal principal = toPrincipal(user);
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                principal,
                null,
                List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()))
        );

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
        if (request.getSession(false) != null) {
            request.changeSessionId();
        }
        securityContextRepository.saveContext(context, request, response);

        return toResponse(user);
    }

    @GetMapping("/me")
    public AuthUserResponse currentUser(
            @AuthenticationPrincipal AppUserPrincipal principal
    ) {
        return new AuthUserResponse(
                principal.getUserId(), principal.getEmail(), principal.getDisplayName(),
                principal.getRole(), principal.getInstitutionId()
        );
    }

    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void logout(HttpServletRequest request) {
        SecurityContextHolder.clearContext();
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
    }

    @ExceptionHandler(EmailLoginService.InvalidLoginTokenException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public Map<String, String> invalidToken(EmailLoginService.InvalidLoginTokenException exception) {
        return Map.of("message", exception.getMessage());
    }

    private AppUserPrincipal toPrincipal(Users user) {
        return new AppUserPrincipal(
                user.getId(), user.getEmail(), user.getDisplayName(), user.getRole(), user.getInstitution().getId()
        );
    }

    private AuthUserResponse toResponse(Users user) {
        return new AuthUserResponse(
                user.getId(), user.getEmail(), user.getDisplayName(), user.getRole(), user.getInstitution().getId()
        );
    }
}
