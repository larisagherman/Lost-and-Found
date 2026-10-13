package backend.dto;

import backend.model.ItemStatus;
import backend.model.ItemType;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ItemReportDtoResponse {
    private Long id;
    private String itemName;
    private String itemDetails;
    private ItemType itemType;
    private String location;
    private ItemStatus itemStatus;
    private LocalDateTime createdAt;
}
