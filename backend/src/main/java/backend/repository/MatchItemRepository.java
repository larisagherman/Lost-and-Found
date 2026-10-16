package backend.repository;

import backend.model.MatchItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MatchItemRepository extends JpaRepository<MatchItem, Long> {
    boolean existsMatchItemByLostItemReport_IdAndFoundItemReport_Id(Long lostItemReportId, Long foundItemReportId);
}
