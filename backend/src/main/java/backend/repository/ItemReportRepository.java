package backend.repository;

import backend.model.ItemReport;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemReportRepository extends JpaRepository<ItemReport, Long> {
    ItemReport getItemReportsById(Long id);
}
