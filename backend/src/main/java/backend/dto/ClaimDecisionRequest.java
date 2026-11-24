package backend.dto;

import backend.model.ClaimStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ClaimDecisionRequest {
    @NotNull
    private ClaimStatus status;

    @Size(max = 1000)
    private String reason;
}
