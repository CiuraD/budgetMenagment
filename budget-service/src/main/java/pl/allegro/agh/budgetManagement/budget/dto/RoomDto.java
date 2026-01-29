package pl.allegro.agh.budgetManagement.budget.dto;

import jakarta.validation.constraints.NotBlank;

public class RoomDto {
    private Long roomId;

    @NotBlank
    private String roomName;

    public RoomDto() {
    }

    public RoomDto(Long roomId, String roomName) {
        this.roomId = roomId;
        this.roomName = roomName;
    }

    public Long getRoomId() {
        return roomId;
    }

    public void setRoomId(Long roomId) {
        this.roomId = roomId;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }
}
