package backend.service;

import backend.dto.ItemReportDtoRequest;
import backend.dto.ItemReportDtoResponse;
import backend.mapper.ItemReportMapper;
import backend.model.ItemReport;
import backend.model.ItemType;
import backend.repository.ItemReportRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class ItemReportService {
    private final ItemReportRepository itemReportRepository;
    private final ItemReportMapper itemReportMapper;
    public void createItemReport(ItemReportDtoRequest itemReportDtoRequest) {
        ItemReport itemReport=new ItemReport();
        itemReport.setItemName(itemReportDtoRequest.getItemName());
        itemReport.setItemDetails(itemReportDtoRequest.getItemDetails());
        itemReport.setItemType(itemReportDtoRequest.getItemType());
        itemReport.setLocation(itemReportDtoRequest.getLocation());
        itemReportRepository.save(itemReport);
    }
    public List<ItemReport> getAllItemReports() {
        return itemReportRepository.findAll();
    }
    public List<ItemReportDtoResponse> getAllLostItemReports() {
        List<ItemReport> itemReports=itemReportRepository
                .findAll()
                .stream()
                .filter(itemReport -> itemReport.getItemType().equals(ItemType.LOST)).toList();
    return itemReportMapper.toItemReportDtoResponseList(itemReports);
    }
    public List<ItemReportDtoResponse> getAllFoundItemReports(){
        List<ItemReport> itemReports=itemReportRepository
                .findAll()
                .stream()
                .filter(itemReport->itemReport.getItemType().equals(ItemType.FOUND))
                .toList();
        return itemReportMapper.toItemReportDtoResponseList(itemReports);
    }
}
