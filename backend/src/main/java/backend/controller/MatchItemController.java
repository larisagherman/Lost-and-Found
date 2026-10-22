package backend.controller;

import backend.dto.MatchItemDtoRequest;
import backend.dto.MatchItemDtoResponse;
import backend.service.MatchItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/match-items")
@RequiredArgsConstructor
public class MatchItemController {
    private final MatchItemService matchItemService;
    @PostMapping
    public MatchItemDtoResponse createMatch(@RequestBody MatchItemDtoRequest matchItemDtoRequest) {
        System.out.println("Lost ID: " + matchItemDtoRequest.getLostItemReportId());
        System.out.println("Found ID: " + matchItemDtoRequest.getFoundItemReportId());
        return matchItemService.createMatch(matchItemDtoRequest);
    }
    @DeleteMapping
    public void deleteMatch(@RequestParam Long matchItemId) {
        matchItemService.deleteMatch(matchItemId);
    }
}
