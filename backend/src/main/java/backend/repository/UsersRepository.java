package backend.repository;

import backend.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsersRepository extends JpaRepository<Users, Long> {
    java.util.Optional<Users> findByEmailIgnoreCase(String email);
}
