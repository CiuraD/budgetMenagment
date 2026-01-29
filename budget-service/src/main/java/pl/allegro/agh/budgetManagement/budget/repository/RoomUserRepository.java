package pl.allegro.agh.budgetManagement.budget.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.allegro.agh.budgetManagement.budget.model.RoomUser;
import pl.allegro.agh.budgetManagement.budget.model.RoomUserId;

import java.util.List;

public interface RoomUserRepository extends JpaRepository<RoomUser, RoomUserId> {
    List<RoomUser> findRoomUserById_UserId(Long userId);
}

