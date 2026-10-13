package backend.controller;

import backend.dto.ItemReportDtoRequest;
import backend.model.ItemReport;
import backend.service.ItemReportService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/item-reports")
@AllArgsConstructor
public class ItemReportController {
    private final ItemReportService itemReportService;
    @PostMapping
    public void createItemReport(ItemReportDtoRequest itemReportDtoRequest) {
        itemReportService.createItemReport(itemReportDtoRequest);
    }

    @GetMapping
    public List<ItemReport> getAllItemReports() {
        return itemReportService.getAllItemReports();
    }
}
