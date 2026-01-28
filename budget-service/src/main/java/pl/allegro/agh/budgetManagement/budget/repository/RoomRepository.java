package pl.allegro.agh.budgetManagement.budget.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.allegro.agh.budgetManagement.budget.model.Room;

public interface RoomRepository extends JpaRepository<Room, Long> {
}

