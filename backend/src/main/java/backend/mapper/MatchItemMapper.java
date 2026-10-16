package backend.mapper;

import backend.dto.MatchItemDtoResponse;
import backend.model.MatchItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MatchItemMapper {
    @Mapping(source = "lostItemReportId.id", target = "lostItemReportId")
    @Mapping(source = "foundItemReportId.id", target = "foundItemReportId")
    MatchItemDtoResponse toMatchItemDtoResponse(MatchItem matchItem) ;
}
