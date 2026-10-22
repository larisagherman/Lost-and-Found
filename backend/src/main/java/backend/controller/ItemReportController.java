package backend.controller;

import backend.dto.ItemReportDtoRequest;
import backend.dto.ItemReportDtoResponse;
import backend.model.ItemReport;
import backend.service.ItemReportService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/item-reports")
@AllArgsConstructor
public class ItemReportController {
    private final ItemReportService itemReportService;
    @PostMapping
    public void createItemReport(@RequestBody ItemReportDtoRequest itemReportDtoRequest) {
        itemReportService.createItemReport(itemReportDtoRequest);
    }
    //TODO: remove this request param and get the institutionId from the logged in user
    @GetMapping
    public List<ItemReportDtoResponse> getAllItemReports(
            @RequestParam Long institutionId) {

        return itemReportService.getAllItemReports(institutionId);
    }
    //TODO: remove this request param and get the institutionId from the logged in user
    @GetMapping("/lost")
    public List<ItemReportDtoResponse> getAllLostItemReports(@RequestParam Long institutionId) {
        return itemReportService.getAllLostItemReports(institutionId);
    }
    //TODO: remove this request param and get the institutionId from the logged in user
    @GetMapping("/found")
    public List<ItemReportDtoResponse> getAllFoundItemReports(@RequestParam Long institutionId) {
        return itemReportService.getAllFoundItemReports(institutionId);
    }

}
