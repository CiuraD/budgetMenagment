package pl.allegro.agh.budgetManagement.budget.service;

import org.springframework.stereotype.Service;
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

import jakarta.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoomService {

    private final RoomRepository roomRepository;
    private final RoomUserRepository roomUserRepository;
    private final RoomProductRepository roomProductRepository;
    private final TransactionLogger transactionLogger;

    public RoomService(RoomRepository roomRepository,
                       RoomUserRepository roomUserRepository,
                       RoomProductRepository roomProductRepository,
                       TransactionLogger transactionLogger) {
        this.roomRepository = roomRepository;
        this.roomUserRepository = roomUserRepository;
        this.roomProductRepository = roomProductRepository;
        this.transactionLogger = transactionLogger;
    }

    @Transactional
    public RoomDto createRoom(RoomDto request) {
        Room room = new Room();
        room.setRoomName(request.getRoomName());
        Room saved = roomRepository.save(room);
        RoomDto dto = new RoomDto(saved.getRoomId(), saved.getRoomName());
        transactionLogger.log("create_room", dto);
        return dto;
    }

    public List<RoomDto> listRooms() {
        return roomRepository.findAll().stream()
                .map(r -> new RoomDto(r.getRoomId(), r.getRoomName()))
                .collect(Collectors.toList());
    }

    @Transactional
    public RoomUserDto addUserToRoom(Long roomId, RoomUserDto request) {
        Room room = roomRepository.findById(roomId).orElseThrow(() -> new ResourceNotFoundException("Room not found"));
        RoomUserId id = new RoomUserId(request.getUserId(), roomId);
        RoomUser ru = new RoomUser();
        ru.setId(id);
        ru.setRoom(room);
        ru.setAdmin(request.isAdmin());
        RoomUser saved = roomUserRepository.save(ru);
        RoomUserDto dto = new RoomUserDto(saved.getUserId(), saved.getRoomId(), saved.isAdmin());
        transactionLogger.log("add_user_to_room", dto);
        return dto;
    }

    @Transactional
    public RoomProductDto addProduct(Long roomId, RoomProductDto request) {
        Room room = roomRepository.findById(roomId).orElseThrow(() -> new ResourceNotFoundException("Room not found"));
        RoomProduct p = new RoomProduct();
        p.setRoom(room);
        p.setProductName(request.getProductName());
        p.setPrice(request.getPrice());
        p.setPaid(request.isPaid());
        RoomProduct saved = roomProductRepository.save(p);
        RoomProductDto dto = new RoomProductDto(saved.getProductId(), roomId, saved.getProductName(), saved.getPrice(), saved.isPaid());
        transactionLogger.log("add_product", dto);
        return dto;
    }

    public List<RoomProductDto> listProducts(Long roomId) {
        if (!roomRepository.existsById(roomId)) {
            throw new ResourceNotFoundException("Room not found");
        }
        return roomProductRepository.findByRoomRoomId(roomId).stream()
                .map(p -> new RoomProductDto(p.getProductId(), p.getRoom().getRoomId(), p.getProductName(), p.getPrice(), p.isPaid()))
                .collect(Collectors.toList());
    }
}
