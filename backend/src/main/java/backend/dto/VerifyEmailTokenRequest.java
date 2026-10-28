package backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class VerifyEmailTokenRequest {
    @NotBlank
    private String token;
}
