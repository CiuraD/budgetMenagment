package pl.allegro.agh.budgetManagement.budget.model;

import jakarta.persistence.*;

@Entity
@Table(name = "room_users")
public class RoomUser {

    @EmbeddedId
    private RoomUserId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("roomId")
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    @Column(name = "is_admin", nullable = false)
    private boolean isAdmin = false;

    public RoomUser() {
    }

    public RoomUser(RoomUserId id, Room room, boolean isAdmin) {
        this.id = id;
        this.room = room;
        this.isAdmin = isAdmin;
    }

    public RoomUserId getId() {
        return id;
    }

    public void setId(RoomUserId id) {
        this.id = id;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    public Long getUserId() {
        return id != null ? id.getUserId() : null;
    }

    public Long getRoomId() {
        return id != null ? id.getRoomId() : null;
    }
}

