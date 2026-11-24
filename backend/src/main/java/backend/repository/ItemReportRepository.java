package backend.repository;

import backend.model.ItemReport;
import backend.model.ItemReportStatus;
import backend.model.ItemReportType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;

import java.util.List;

public interface ItemReportRepository extends JpaRepository<ItemReport, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select report from ItemReport report where report.id = :id")
    java.util.Optional<ItemReport> findByIdForUpdate(@Param("id") Long id);

    List<ItemReport> findByReporterInstitutionIdAndItemReportStatus(
            Long institutionId,
            ItemReportStatus itemReportStatus
    );

    List<ItemReport> findByReporterInstitutionIdAndItemReportTypeAndItemReportStatus(
            Long institutionId,
            ItemReportType itemReportType,
            ItemReportStatus itemReportStatus
    );

    List<ItemReport> findByReporterIdOrderByCreatedAtDesc(Long reporterId);

    boolean existsByIdAndItemReportType(Long id, ItemReportType itemReportType);
}
