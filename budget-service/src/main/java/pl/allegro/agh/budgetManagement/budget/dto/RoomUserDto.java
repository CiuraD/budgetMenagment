package pl.allegro.agh.budgetManagement.budget.dto;

import jakarta.validation.constraints.NotNull;

public class RoomUserDto {
    @NotNull
    private Long userId;
    private Long roomId;
    private boolean isAdmin;

    public RoomUserDto() {
    }

    public RoomUserDto(Long userId, Long roomId, boolean isAdmin) {
        this.userId = userId;
        this.roomId = roomId;
        this.isAdmin = isAdmin;
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

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }
}
