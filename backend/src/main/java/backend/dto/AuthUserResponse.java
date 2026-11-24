package backend.dto;

import backend.model.UserRole;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuthUserResponse {
    private final Long id;
    private final String email;
    private final String displayName;
    private final UserRole role;
    private final Long institutionId;
}
