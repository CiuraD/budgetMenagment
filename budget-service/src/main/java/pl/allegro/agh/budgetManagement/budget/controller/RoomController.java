package pl.allegro.agh.budgetManagement.budget.controller;

import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.allegro.agh.budgetManagement.budget.dto.RoomDto;
import pl.allegro.agh.budgetManagement.budget.dto.RoomProductDto;
import pl.allegro.agh.budgetManagement.budget.dto.RoomUserDto;
import pl.allegro.agh.budgetManagement.budget.service.RoomService;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;

import java.net.URI;
import java.util.List;

@RestController
@Profile("budget")
@RequestMapping("/rooms")
public class RoomController {

    private final RoomService roomService;

    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    @Operation(summary = "Create a room")
    @PostMapping
    public ResponseEntity<RoomDto> createRoom(@Valid @RequestBody RoomDto request) {
        RoomDto dto = roomService.createRoom(request);
        return ResponseEntity.created(URI.create("/rooms/" + dto.getRoomId())).body(dto);
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
        return ResponseEntity.ok(dto);
    }

    @Operation(summary = "Add product to room")
    @PostMapping("/{roomId}/products")
    public ResponseEntity<RoomProductDto> addProduct(@PathVariable Long roomId, @Valid @RequestBody RoomProductDto request) {
        RoomProductDto dto = roomService.addProduct(roomId, request);
        return ResponseEntity.created(URI.create("/rooms/" + roomId + "/products/" + dto.getProductId())).body(dto);
    }

    @Operation(summary = "List products in a room")
    @GetMapping("/{roomId}/products")
    public List<RoomProductDto> listProducts(@PathVariable Long roomId) {
        return roomService.listProducts(roomId);
    }
}
