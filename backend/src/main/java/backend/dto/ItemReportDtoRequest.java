package backend.dto;

import backend.model.ItemStatus;
import backend.model.ItemType;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ItemReportDtoRequest {
    private String itemName;
    private String itemDetails;
    private ItemType itemType;
    private String location;
}
