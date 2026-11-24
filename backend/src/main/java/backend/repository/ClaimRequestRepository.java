package backend.repository;

import backend.model.ClaimRequest;
import backend.model.ClaimStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;

import java.util.List;

public interface ClaimRequestRepository extends JpaRepository<ClaimRequest, Long> {
    boolean existsByFoundItemReport_Id(Long reportId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select claim from ClaimRequest claim where claim.id = :id")
    java.util.Optional<ClaimRequest> findByIdForUpdate(@Param("id") Long id);

    boolean existsByFoundItemReport_IdAndClaimant_Id(Long reportId, Long claimantId);

    boolean existsByFoundItemReport_IdAndStatus(Long reportId, ClaimStatus status);

    boolean existsByFoundItemReport_IdAndStatusIn(Long reportId, List<ClaimStatus> statuses);

    List<ClaimRequest> findByClaimant_IdOrderByCreatedAtDesc(Long claimantId);

    List<ClaimRequest> findByFoundItemReport_Reporter_Institution_IdAndStatusOrderByCreatedAtAsc(
            Long institutionId,
            ClaimStatus status
    );

    List<ClaimRequest> findByFoundItemReport_Reporter_Institution_IdOrderByCreatedAtAsc(Long institutionId);

    List<ClaimRequest> findByFoundItemReport_IdAndStatus(Long reportId, ClaimStatus status);
}
