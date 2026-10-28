package backend.security;

import backend.model.UserRole;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AppUserPrincipal {
    private final Long userId;
    private final String email;
    private final String displayName;
    private final UserRole role;
    private final Long institutionId;
}
