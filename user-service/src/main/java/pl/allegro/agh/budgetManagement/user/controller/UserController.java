package pl.allegro.agh.budgetManagement.user.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.allegro.agh.budgetManagement.user.dto.AuthResponse;
import pl.allegro.agh.budgetManagement.user.dto.LoginRequest;
import pl.allegro.agh.budgetManagement.user.dto.RegistrationRequest;
import pl.allegro.agh.budgetManagement.user.service.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/hello")
    public ResponseEntity<?> hello() {
        return ResponseEntity.ok("hello from user-service");
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(
            @Valid @RequestBody RegistrationRequest req
    ) {
        log.info("Register attempt for username: {}", req.getUsername());
        try {
            AuthResponse response = userService.register(req);
            log.info("User registered successfully: username={}, userId={}", req.getUsername(), response.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException ex) {
            log.warn("Unexpected error while registering username={}: {}", req.getUsername(), ex.getMessage());
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .build();
        } catch (Exception ex) {
            log.error("Unexpected error while registering username={}: {}", req.getUsername(), ex.getMessage());
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .build();
        }
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody LoginRequest req
    ) {
        log.info("Login attempt for username: {}", req.getUsername());
        return userService.authenticate(req)
                .map(resp -> {
                    log.info("User authenticated successfully: username={}, userId={}", req.getUsername(), resp.getId());
                    return ResponseEntity.ok(resp);
                })
                .orElseGet(() -> {
                    log.warn("Authentication failed for username: {}", req.getUsername());
                    return ResponseEntity
                            .status(HttpStatus.UNAUTHORIZED)
                            .build();
                  }
                );
    }

    @GetMapping("/me")
    public ResponseEntity<String> me() {
        return ResponseEntity.ok("You are authenticated");
    }

}
