package backend.repository;

import backend.model.Users;
import backend.model.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UsersRepository extends JpaRepository<Users, Long> {
    java.util.Optional<Users> findByEmailIgnoreCase(String email);
    List<Users> findByInstitution_IdAndRole(Long institutionId, UserRole role);
}
