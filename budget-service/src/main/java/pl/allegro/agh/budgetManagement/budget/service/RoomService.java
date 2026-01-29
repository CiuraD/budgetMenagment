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

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoomService {

    private final RoomRepository roomRepository;
    private final RoomUserRepository roomUserRepository;
    private final RoomProductRepository roomProductRepository;

    public RoomService(RoomRepository roomRepository,
                       RoomUserRepository roomUserRepository,
                       RoomProductRepository roomProductRepository) {
        this.roomRepository = roomRepository;
        this.roomUserRepository = roomUserRepository;
        this.roomProductRepository = roomProductRepository;
    }

    @Transactional
    public RoomDto createRoom(String roomName) {
        Room room = new Room();
        room.setRoomName(roomName);
        Room saved = roomRepository.save(room);
        return new RoomDto(saved.getRoomId(), saved.getRoomName());
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
        return new RoomUserDto(saved.getUserId(), saved.getRoomId(), saved.isAdmin());
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
        return new RoomProductDto(saved.getProductId(), roomId, saved.getProductName(), saved.getPrice(), saved.isPaid());
    }

    public List<RoomProductDto> listProducts(Long roomId) {
        if (!roomRepository.existsById(roomId)) {
            throw new ResourceNotFoundException("Room not found");
        }
        return roomProductRepository.findByRoomRoomId(roomId).stream()
                .map(p -> new RoomProductDto(p.getProductId(), p.getRoom().getRoomId(), p.getProductName(), p.getPrice(), p.isPaid()))
                .collect(Collectors.toList());
    }

    public BigDecimal getRoomTotal(Long roomId) {
        if (!roomRepository.existsById(roomId)) {
            throw new ResourceNotFoundException("Room not found");
        }
        List<RoomProduct> products = roomProductRepository.findByRoomRoomId(roomId);
        return products.stream()
                .map(RoomProduct::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getPaidRoomTotal(Long roomId) {
        if (!roomRepository.existsById(roomId)) {
            throw new ResourceNotFoundException("Room not found");
        }
        List<RoomProduct> paidProducts = roomProductRepository.findByRoomRoomIdAndIsPaidTrue(roomId);
        return paidProducts.stream()
                .map(RoomProduct::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public RoomProductDto getProductById(Long roomId, Long productId) {
        if (!roomRepository.existsById(roomId)) {
            throw new ResourceNotFoundException("Room not found");
        }
        RoomProduct product = roomProductRepository.findByProductIdAndRoomRoomId(productId, roomId);
        return new RoomProductDto(product.getProductId(), roomId, product.getProductName(), product.getPrice(), product.isPaid());
    }

    public RoomProductDto markProductAsPaid(Long roomId, Long productId) {
        var product = roomProductRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        if (product.getRoom() == null || !product.getRoom().getRoomId().equals(roomId)) {
            throw new ResourceNotFoundException("Product not found in the specified room");
        }

        if (!product.isPaid()) {
            product.setPaid(true);
            roomProductRepository.save(product);
        }

        return getProductById(roomId, productId);
    }

    public BigDecimal getUnpaidRoomTotal(Long roomId) {
        if (!roomRepository.existsById(roomId)) {
            throw new ResourceNotFoundException("Room not found");
        }
        List<RoomProduct> unpaidProducts = roomProductRepository.findByRoomRoomIdAndIsPaidFalse(roomId);
        return unpaidProducts.stream()
                .map(RoomProduct::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}

