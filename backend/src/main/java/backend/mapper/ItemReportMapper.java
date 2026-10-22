package backend.mapper;

import backend.dto.ItemReportDtoRequest;
import backend.dto.ItemReportDtoResponse;
import backend.model.ItemReport;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ItemReportMapper{
    @Mapping(source = "reporter.id", target = "reporterId")
    ItemReportDtoResponse toItemReportDtoResponse(ItemReport itemReport);
    List<ItemReportDtoResponse> toItemReportDtoResponseList(List<ItemReport> itemReports);

}
