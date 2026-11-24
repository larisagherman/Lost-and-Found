package backend.dto;

import backend.model.ItemReportStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ChangeItemReportStatusRequest {
    @NotNull
    private ItemReportStatus status;
}
