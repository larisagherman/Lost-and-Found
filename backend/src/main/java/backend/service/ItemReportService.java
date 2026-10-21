package backend.service;

import backend.dto.ItemReportDtoRequest;
import backend.dto.ItemReportDtoResponse;
import backend.mapper.ItemReportMapper;
import backend.model.ItemReport;
import backend.model.ItemReportType;
import backend.model.Users;
import backend.repository.ItemReportRepository;
import backend.repository.UsersRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class ItemReportService {
    private final ItemReportRepository itemReportRepository;
    private final UsersRepository usersRepository;
    private final ItemReportMapper itemReportMapper;
    public void createItemReport(ItemReportDtoRequest itemReportDtoRequest) {
        Users reporter = usersRepository.findById(itemReportDtoRequest.getReporterId())
                .orElseThrow(() -> new IllegalArgumentException("Reporter not found."));
        ItemReport itemReport=new ItemReport();
        itemReport.setReporter(reporter);
        itemReport.setItemName(itemReportDtoRequest.getItemName());
        itemReport.setItemDetails(itemReportDtoRequest.getItemDetails());
        itemReport.setItemReportType(itemReportDtoRequest.getItemReportType());
        itemReport.setLocation(itemReportDtoRequest.getLocation());
        itemReport.setIncidentAt(itemReportDtoRequest.getIncidentAt());
        itemReport.setItemDetails(itemReportDtoRequest.getItemDetails());
        itemReport.setCategory(itemReportDtoRequest.getCategory());
        itemReport.setColour(itemReportDtoRequest.getColour());
        itemReport.setBrand(itemReportDtoRequest.getBrand());
        itemReport.setIncidentAt(itemReportDtoRequest.getIncidentAt());
        itemReportRepository.save(itemReport);
    }
    public List<ItemReportDtoResponse> getAllItemReports() {
        return itemReportMapper.toItemReportDtoResponseList(itemReportRepository.findAll());
    }
    public List<ItemReportDtoResponse> getAllLostItemReports() {
        List<ItemReport> itemReports=itemReportRepository
                .findAll()
                .stream()
                .filter(itemReport -> itemReport.getItemReportType().equals(ItemReportType.LOST)).toList();
    return itemReportMapper.toItemReportDtoResponseList(itemReports);
    }
    public List<ItemReportDtoResponse> getAllFoundItemReports(){
        List<ItemReport> itemReports=itemReportRepository
                .findAll()
                .stream()
                .filter(itemReport->itemReport.getItemReportType().equals(ItemReportType.FOUND))
                .toList();
        return itemReportMapper.toItemReportDtoResponseList(itemReports);
    }
}
