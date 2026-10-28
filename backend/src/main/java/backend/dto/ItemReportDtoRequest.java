package backend.dto;

import backend.model.ItemReportType;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ItemReportDtoRequest {
    private String itemName;
    private String itemDetails;
    private String category;
    private String colour;
    private String brand;
    private LocalDateTime incidentAt;
    private ItemReportType itemReportType;
    private String location;
}
