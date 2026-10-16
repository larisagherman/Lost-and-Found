package backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table
@Getter
@Setter
@NoArgsConstructor
public class MatchItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name="lost_item_report_id",nullable = false)
    private ItemReport lostItemReportId;
    @ManyToOne
    @JoinColumn(name="found_item_report_id",nullable = false)
    private ItemReport foundItemReportId;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MatchStatus matchStatus;
    @Column(nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if(matchStatus == null) {
            matchStatus=MatchStatus.SUGGESTED;
        }
    }

}
