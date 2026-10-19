package backend.dto;

import backend.model.ItemReportType;
import backend.model.ItemReportStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ItemReportDtoResponse {
    private Long id;
    private String itemName;
    private String itemDetails;
    private String category;
    private String colour;
    private String brand;
    private String location;
    private LocalDateTime incidentAt;
    private ItemReportType itemReportType;
    private ItemReportStatus itemReportStatus;
    private LocalDateTime createdAt;
}
