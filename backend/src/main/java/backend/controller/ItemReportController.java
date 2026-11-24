package backend.controller;

import backend.dto.ItemReportDtoRequest;
import backend.dto.ItemReportDtoResponse;
import backend.dto.ChangeItemReportStatusRequest;
import backend.security.AppUserPrincipal;
import backend.service.ItemReportService;
import jakarta.validation.Valid;
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

    @GetMapping("/mine")
    public List<ItemReportDtoResponse> getMyItemReports(@AuthenticationPrincipal AppUserPrincipal principal) {
        return itemReportService.getMyItemReports(principal.getUserId());
    }

    @PutMapping("/{itemReportId}")
    public ItemReportDtoResponse updateMyItemReport(
            @PathVariable Long itemReportId,
            @Valid @RequestBody ItemReportDtoRequest request,
            @AuthenticationPrincipal AppUserPrincipal principal
    ) {
        return itemReportService.updateMyItemReport(itemReportId, principal.getUserId(), request);
    }

    @PatchMapping("/{itemReportId}/status")
    public ItemReportDtoResponse changeMyItemReportStatus(
            @PathVariable Long itemReportId,
            @Valid @RequestBody ChangeItemReportStatusRequest request,
            @AuthenticationPrincipal AppUserPrincipal principal
    ) {
        return itemReportService.changeMyItemReportStatus(
                itemReportId, principal.getUserId(), request.getStatus()
        );
    }

}
