package backend.mapper;

import backend.dto.ItemReportDtoRequest;
import backend.dto.ItemReportDtoResponse;
import backend.model.ItemReport;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ItemReportMapper{
    ItemReportDtoResponse toItemReportDtoResponse(ItemReport itemReport);

}
