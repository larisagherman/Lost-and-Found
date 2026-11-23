package backend.controller;

import backend.dto.ItemReportDtoRequest;
import backend.dto.ItemReportDtoResponse;
import backend.security.AppUserPrincipal;
import backend.service.ItemReportService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/item-reports")
@AllArgsConstructor
public class ItemReportController {
    private final ItemReportService itemReportService;
    @PostMapping
    public void createItemReport(
            @RequestBody ItemReportDtoRequest itemReportDtoRequest,
            @AuthenticationPrincipal AppUserPrincipal principal
    ) {
        itemReportService.createItemReport(itemReportDtoRequest, principal.getUserId());
    }
    @GetMapping
    public List<ItemReportDtoResponse> getAllItemReports(@AuthenticationPrincipal AppUserPrincipal principal) {
        return itemReportService.getAllItemReports(principal.getInstitutionId());
    }
    @GetMapping("/lost")
    public List<ItemReportDtoResponse> getAllLostItemReports(@AuthenticationPrincipal AppUserPrincipal principal) {
        return itemReportService.getAllLostItemReports(principal.getInstitutionId());
    }
    @GetMapping("/found")
    public List<ItemReportDtoResponse> getAllFoundItemReports(@AuthenticationPrincipal AppUserPrincipal principal) {
        return itemReportService.getAllFoundItemReports(principal.getInstitutionId());
    }

}
