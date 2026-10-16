package backend.dto;

import lombok.Data;

@Data
public class MatchItemDtoRequest {
    private Long LostItemReportId;
    private Long FoundItemReportId;
}
