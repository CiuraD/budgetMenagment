package pl.allegro.agh.budgetManagement.budget.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.allegro.agh.budgetManagement.budget.dto.RoomDto;
import pl.allegro.agh.budgetManagement.budget.dto.RoomProductDto;
import pl.allegro.agh.budgetManagement.budget.dto.RoomUserDto;
import pl.allegro.agh.budgetManagement.budget.exception.ResourceNotFoundException;
import pl.allegro.agh.budgetManagement.budget.model.Room;
import pl.allegro.agh.budgetManagement.budget.model.RoomProduct;
import pl.allegro.agh.budgetManagement.budget.model.RoomUser;
import pl.allegro.agh.budgetManagement.budget.model.RoomUserId;
import pl.allegro.agh.budgetManagement.budget.repository.RoomProductRepository;
import pl.allegro.agh.budgetManagement.budget.repository.RoomRepository;
import pl.allegro.agh.budgetManagement.budget.repository.RoomUserRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoomServiceTest {

    @Mock
    RoomRepository roomRepository;

    @Mock
    RoomUserRepository roomUserRepository;

    @Mock
    RoomProductRepository roomProductRepository;

    @InjectMocks
    RoomService roomService;

    @Test
    void createRoom_savesAndReturnsDto() {
        RoomDto req = new RoomDto(null, "MyRoom");
        Room saved = new Room();
        saved.setRoomId(1L);
        saved.setRoomName("MyRoom");

        when(roomRepository.save(any(Room.class))).thenReturn(saved);

        RoomDto resp = roomService.createRoom(req.getRoomName());

        assertNotNull(resp);
        assertEquals(1L, resp.getRoomId());
        assertEquals("MyRoom", resp.getRoomName());
        verify(roomRepository).save(any(Room.class));
    }

    @Test
    void listRooms_mapsAllRooms() {
        Room r1 = new Room(); r1.setRoomId(1L); r1.setRoomName("A");
        Room r2 = new Room(); r2.setRoomId(2L); r2.setRoomName("B");
        when(roomRepository.findAll()).thenReturn(List.of(r1, r2));

        List<RoomDto> list = roomService.listRooms();
        assertEquals(2, list.size());
        assertEquals("A", list.get(0).getRoomName());
    }

    @Test
    void addUserToRoom_throwsWhenRoomNotFound() {
        when(roomRepository.findById(10L)).thenReturn(Optional.empty());
        RoomUserDto req = new RoomUserDto(5L, null, true);
        assertThrows(ResourceNotFoundException.class, () -> roomService.addUserToRoom(10L, req));
    }

    @Test
    void addUserToRoom_savesAndReturnsDto() {
        Room room = new Room(); room.setRoomId(2L);
        when(roomRepository.findById(2L)).thenReturn(Optional.of(room));

        RoomUser saved = new RoomUser();
        RoomUserId id = new RoomUserId(7L, 2L);
        saved.setId(id);
        saved.setRoom(room);
        saved.setAdmin(true);

        when(roomUserRepository.save(any(RoomUser.class))).thenReturn(saved);

        RoomUserDto req = new RoomUserDto(7L, null, true);
        RoomUserDto resp = roomService.addUserToRoom(2L, req);

        assertEquals(7L, resp.getUserId());
        assertTrue(resp.isAdmin());
        verify(roomUserRepository).save(any(RoomUser.class));
    }

    @Test
    void addProduct_throwsWhenRoomNotFound() {
        when(roomRepository.findById(99L)).thenReturn(Optional.empty());
        RoomProductDto req = new RoomProductDto(null, null, "X", BigDecimal.TEN, false);
        assertThrows(ResourceNotFoundException.class, () -> roomService.addProduct(99L, req));
    }

    @Test
    void addProduct_savesAndReturnsDto() {
        Room room = new Room(); room.setRoomId(3L);
        when(roomRepository.findById(3L)).thenReturn(Optional.of(room));

        RoomProduct saved = new RoomProduct();
        saved.setProductId(11L);
        saved.setRoom(room);
        saved.setProductName("Milk");
        saved.setPrice(BigDecimal.valueOf(4.5));
        saved.setPaid(false);

        when(roomProductRepository.save(any(RoomProduct.class))).thenReturn(saved);

        RoomProductDto req = new RoomProductDto(null, null, "Milk", BigDecimal.valueOf(4.5), false);
        RoomProductDto resp = roomService.addProduct(3L, req);

        assertEquals(11L, resp.getProductId());
        assertEquals("Milk", resp.getProductName());
        assertEquals(BigDecimal.valueOf(4.5), resp.getPrice());
        verify(roomProductRepository).save(any(RoomProduct.class));
    }

    @Test
    void listProducts_throwsWhenRoomMissing() {
        when(roomRepository.existsById(55L)).thenReturn(false);
        assertThrows(ResourceNotFoundException.class, () -> roomService.listProducts(55L));
    }

    @Test
    void listProducts_returnsMappedProducts() {
        Room room = new Room(); room.setRoomId(4L);
        RoomProduct p1 = new RoomProduct(); p1.setProductId(21L); p1.setRoom(room); p1.setProductName("A"); p1.setPrice(BigDecimal.ONE); p1.setPaid(true);
        RoomProduct p2 = new RoomProduct(); p2.setProductId(22L); p2.setRoom(room); p2.setProductName("B"); p2.setPrice(BigDecimal.TEN); p2.setPaid(false);

        when(roomRepository.existsById(4L)).thenReturn(true);
        when(roomProductRepository.findByRoomRoomId(4L)).thenReturn(List.of(p1, p2));

        var list = roomService.listProducts(4L);
        assertEquals(2, list.size());
        assertEquals("A", list.get(0).getProductName());
    }
}

