package pl.allegro.agh.budgetManagement.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.allegro.agh.budgetManagement.user.dto.UserDto;
import pl.allegro.agh.budgetManagement.user.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    List<User> findAllByOrderByUsernameAsc();
}

