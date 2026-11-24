package backend.dto;

import backend.model.ClaimStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ClaimResponse {
    private Long id;
    private Long foundItemReportId;
    private String itemName;
    private String itemLocation;
    private String proofDetails;
    private ClaimStatus status;
    private String adminReason;
    private LocalDateTime createdAt;
    private LocalDateTime reviewedAt;
    private LocalDateTime completedAt;
}
