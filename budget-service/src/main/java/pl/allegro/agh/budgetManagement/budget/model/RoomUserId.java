package pl.allegro.agh.budgetManagement.budget.model;

import jakarta.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public class RoomUserId implements Serializable {
    private Long userId;
    private Long roomId;

    public RoomUserId() {
    }

    public RoomUserId(Long userId, Long roomId) {
        this.userId = userId;
        this.roomId = roomId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getRoomId() {
        return roomId;
    }

    public void setRoomId(Long roomId) {
        this.roomId = roomId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RoomUserId that = (RoomUserId) o;
        return java.util.Objects.equals(userId, that.userId) && java.util.Objects.equals(roomId, that.roomId);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(userId, roomId);
    }
}

