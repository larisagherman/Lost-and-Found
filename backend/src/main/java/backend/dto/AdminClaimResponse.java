package backend.dto;

import backend.model.ClaimStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AdminClaimResponse {
    private Long id;
    private Long foundItemReportId;
    private String itemName;
    private String publicItemDetails;
    private String privateVerificationDetails;
    private String itemLocation;
    private Long lostItemReportId;
    private String lostItemName;
    private String lostItemDetails;
    private Long claimantId;
    private String claimantName;
    private String claimantEmail;
    private String proofDetails;
    private ClaimStatus status;
    private String adminReason;
    private Long finderId;
    private String finderName;
    private String finderEmail;
    private LocalDateTime createdAt;
    private LocalDateTime reviewedAt;
    private LocalDateTime completedAt;
}
