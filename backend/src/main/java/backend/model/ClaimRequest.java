package backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "claim_request", uniqueConstraints = {
        @UniqueConstraint(
                name = "uk_claim_report_claimant",
                columnNames = {"found_item_report_id", "claimant_id"}
        )
}, indexes = {
        @Index(name = "idx_claim_request_status", columnList = "status"),
        @Index(name = "idx_claim_request_report", columnList = "found_item_report_id")
})
@Getter
@Setter
@NoArgsConstructor
public class ClaimRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "found_item_report_id", nullable = false)
    private ItemReport foundItemReport;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "claimant_id", nullable = false)
    private Users claimant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewed_by_admin_id")
    private Users reviewedByAdmin;

    @Column(name = "proof_details", nullable = false, length = 2000)
    private String proofDetails;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ClaimStatus status = ClaimStatus.PENDING;

    @Column(name = "admin_reason", length = 1000)
    private String adminReason;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "reviewed_at")
    private LocalDateTime reviewedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (status == null) {
            status = ClaimStatus.PENDING;
        }
    }
}
