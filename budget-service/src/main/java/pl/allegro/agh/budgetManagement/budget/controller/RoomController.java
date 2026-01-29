package pl.allegro.agh.budgetManagement.budget.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.allegro.agh.budgetManagement.budget.dto.RoomDto;
import pl.allegro.agh.budgetManagement.budget.dto.RoomProductDto;
import pl.allegro.agh.budgetManagement.budget.dto.RoomUserDto;
import pl.allegro.agh.budgetManagement.budget.service.RoomService;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;

import java.math.BigDecimal;
import java.net.URI;
import java.util.List;

@RestController
@Profile("budget")
@RequestMapping("/rooms")
public class RoomController {

    private static final Logger log = LoggerFactory.getLogger(RoomController.class);
    private final RoomService roomService;

    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    @Operation(summary = "Create a room")
    @PostMapping
    public ResponseEntity<RoomDto> createRoom(@Valid @RequestBody RoomDto dto) {
        RoomDto realDto = roomService.createRoom(dto.getRoomName());
        log.info("Created rom: roomId={}, roomName={}", realDto.getRoomId(), realDto.getRoomName());
        return ResponseEntity.created(URI.create("/rooms/" + realDto.getRoomId())).body(realDto);
    }

    @Operation(summary = "List rooms")
    @GetMapping
    public List<RoomDto> listRooms() {
        return roomService.listRooms();
    }

    @Operation(summary = "Add user to room")
    @PostMapping("/{roomId}/users")
    public ResponseEntity<RoomUserDto> addUserToRoom(@PathVariable Long roomId, @Valid @RequestBody RoomUserDto request) {
        RoomUserDto dto = roomService.addUserToRoom(roomId, request);
        log.info("User added to room: roomId={}, userId={}", roomId, dto.getUserId());
        return ResponseEntity.ok(dto);
    }

    @Operation(summary = "Add product to room")
    @PostMapping("/{roomId}/products")
    public ResponseEntity<RoomProductDto> addProduct(@PathVariable Long roomId, @Valid @RequestBody RoomProductDto request) {
        RoomProductDto dto = roomService.addProduct(roomId, request);
        log.info("Product added to room: productId={}, roomId={}, productName={}, price={}", dto.getProductId(), dto.getRoomId(), dto.getProductName(), dto.getPrice());
        return ResponseEntity.created(URI.create("/rooms/" + roomId + "/products/" + dto.getProductId())).body(dto);
    }

    @Operation(summary = "List products in a room")
    @GetMapping("/{roomId}/products")
    public List<RoomProductDto> listProducts(@PathVariable Long roomId) {
        return roomService.listProducts(roomId);
    }

    @Operation(summary = "Get total unpaid amount in a room")
    @GetMapping("/{roomId}/products/unpaid")
    public BigDecimal getTotalUnpaidAmount(@PathVariable Long roomId) {
        log.info("Calculating total unpaid amount for roomId={}", roomId);
        return roomService.getUnpaidRoomTotal(roomId);
    }

    @Operation(summary = "Mark product as paid")
    @PatchMapping("/{roomId}/products/{productId}/pay")
    public ResponseEntity<RoomProductDto> markProductAsPaid(@PathVariable Long roomId, @PathVariable Long productId) {
        RoomProductDto dto = roomService.markProductAsPaid(roomId, productId);
        log.info("Product marked as paid: productId={}, roomId={}, paid={}", dto.getProductId(), dto.getRoomId(), dto.isPaid());
        return ResponseEntity.ok(dto);
    }

    @Operation(summary = "Get a product by ID in a room")
    @GetMapping("/{roomId}/products/{productId}")
    public ResponseEntity<RoomProductDto> getProductById(@PathVariable Long roomId, @PathVariable Long productId) {
        RoomProductDto dto = roomService.getProductById(roomId, productId);
        return ResponseEntity.ok(dto);
    }
}
