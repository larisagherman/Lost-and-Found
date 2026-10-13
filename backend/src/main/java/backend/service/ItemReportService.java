package backend.service;

import backend.dto.ItemReportDtoRequest;
import backend.model.ItemReport;
import backend.repository.ItemReportRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class ItemReportService {
    private final ItemReportRepository itemReportRepository;
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
}
