package pl.allegro.agh.budgetManagement.budget.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.allegro.agh.budgetManagement.budget.model.RoomProduct;

import java.util.List;

public interface RoomProductRepository extends JpaRepository<RoomProduct, Long> {
    List<RoomProduct> findByRoomRoomId(Long roomId);
}

