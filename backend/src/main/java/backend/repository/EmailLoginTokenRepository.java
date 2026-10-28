package backend.repository;

import backend.model.EmailLoginToken;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface EmailLoginTokenRepository extends JpaRepository<EmailLoginToken, Long> {
    List<EmailLoginToken> findAllByUser_IdAndUsedAtIsNull(Long userId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select token from EmailLoginToken token where token.tokenHash = :tokenHash")
    Optional<EmailLoginToken> findByTokenHashForUpdate(@Param("tokenHash") String tokenHash);
}
