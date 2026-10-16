package backend.service;

import backend.dto.MatchItemDtoRequest;
import backend.dto.MatchItemDtoResponse;
import backend.mapper.MatchItemMapper;
import backend.model.ItemReport;
import backend.model.ItemType;
import backend.model.MatchItem;
import backend.repository.ItemReportRepository;
import backend.repository.MatchItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MatchItemService {
    private final MatchItemRepository matchItemRepository;
    private final ItemReportRepository itemReportRepository;
    private final MatchItemMapper matchItemMapper;
    public MatchItemDtoResponse createMatch(MatchItemDtoRequest matchItemDtoRequest) {
        if(matchItemDtoRequest.getLostItemReportId().equals(matchItemDtoRequest.getFoundItemReportId())){
            System.out.println("lost item report id is "+matchItemDtoRequest.getLostItemReportId()+" and found item report id is "+matchItemDtoRequest.getFoundItemReportId());
            throw new IllegalArgumentException("Lost item report ID and found item report ID cannot be the same.");
        }
        ItemReport lostItemReport = itemReportRepository.findById(matchItemDtoRequest.getLostItemReportId()).orElseThrow(()-> new IllegalArgumentException("Lost item report not found."));
        ItemReport foundItemReport = itemReportRepository.findById(matchItemDtoRequest.getFoundItemReportId()).orElseThrow(()-> new IllegalArgumentException("Found item report not found."));
        if(lostItemReport.getItemType().equals(ItemType.FOUND)){
            throw new IllegalArgumentException("First item report must be of type LOST.");
        }
        if(foundItemReport.getItemType().equals(ItemType.LOST)){
            throw new IllegalArgumentException("Second item report must be of type FOUND.");
        }
        if(matchItemRepository.existsMatchItemByLostItemReport_IdAndFoundItemReport_Id(matchItemDtoRequest.getLostItemReportId(), matchItemDtoRequest.getFoundItemReportId())){
            throw new IllegalArgumentException("Match already exists.");
        }

        MatchItem matchItem=new MatchItem();
        matchItem.setLostItemReport(lostItemReport);
        matchItem.setFoundItemReport(foundItemReport);
        matchItemRepository.save(matchItem);
        return matchItemMapper.toMatchItemDtoResponse(matchItem);
    }
    public void deleteMatch(Long matchItemId) {
        MatchItem matchItem = matchItemRepository.findById(matchItemId).orElseThrow(() -> new IllegalArgumentException("Match item not found."));
        matchItemRepository.delete(matchItem);
    }
}
