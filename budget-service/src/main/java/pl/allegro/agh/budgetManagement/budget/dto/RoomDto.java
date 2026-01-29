package pl.allegro.agh.budgetManagement.budget.dto;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;

public class RoomDto {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
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
