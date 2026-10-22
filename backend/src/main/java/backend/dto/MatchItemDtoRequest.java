package backend.dto;

import lombok.Data;

@Data
public class MatchItemDtoRequest {
    private Long lostItemReportId;
    private Long foundItemReportId;
}
