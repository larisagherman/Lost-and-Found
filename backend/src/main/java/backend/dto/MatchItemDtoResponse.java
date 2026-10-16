package backend.dto;

import backend.model.MatchStatus;
import lombok.Data;

import java.time.LocalDateTime;
@Data
public class MatchItemDtoResponse {
    private Long id;
    private Long lostItemReportId;
    private Long foundItemReportId;
    private MatchStatus matchStatus;
    private LocalDateTime createdAt;
}
