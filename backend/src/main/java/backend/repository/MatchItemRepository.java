package backend.repository;

import backend.model.MatchItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MatchItemRepository extends JpaRepository<MatchItem, Long> {
}
