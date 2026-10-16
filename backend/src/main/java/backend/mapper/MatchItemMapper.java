package backend.mapper;

import backend.dto.MatchItemDtoResponse;
import backend.model.MatchItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MatchItemMapper {
    @Mapping(source = "lostItemReport.id", target = "lostItemReportId")
    @Mapping(source = "foundItemReport.id", target = "foundItemReportId")
    MatchItemDtoResponse toMatchItemDtoResponse(MatchItem matchItem) ;
}
