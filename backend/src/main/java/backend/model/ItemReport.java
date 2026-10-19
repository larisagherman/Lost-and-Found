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
public class ItemReport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String itemName;
    @Column
    private String itemDetails;
    @Column
    private String category;
    @Column
    private String colour;
    @Column
    private String brand;
    @Column(nullable = false)
    private String location;
    @Column
    private LocalDateTime incidentAt;


    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ItemReportType itemReportType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ItemReportStatus itemReportStatus = ItemReportStatus.OPEN;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        if (itemReportStatus == null) {
            itemReportStatus = ItemReportStatus.OPEN;
        }

        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

}
