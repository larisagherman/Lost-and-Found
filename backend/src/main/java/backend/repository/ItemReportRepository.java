package backend.repository;

import backend.model.ItemReport;
import backend.model.ItemReportType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItemReportRepository extends JpaRepository<ItemReport, Long> {
    ItemReport getItemReportsById(Long id);
    List<ItemReport> findByReporterInstitutionId(Long institutionId);

    List<ItemReport> findByReporterInstitutionIdAndItemReportType(
            Long institutionId,
            ItemReportType itemReportType
    );

}
