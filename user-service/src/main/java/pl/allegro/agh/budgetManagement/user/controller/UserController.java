package pl.allegro.agh.budgetManagement.user.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import pl.allegro.agh.budgetManagement.user.dto.AuthResponse;
import pl.allegro.agh.budgetManagement.user.dto.LoginRequest;
import pl.allegro.agh.budgetManagement.user.dto.RegistrationRequest;
import pl.allegro.agh.budgetManagement.user.security.JwtUtil;
import pl.allegro.agh.budgetManagement.user.model.Role;
import pl.allegro.agh.budgetManagement.user.service.UserService;
import pl.allegro.agh.budgetManagement.user.security.RequireRole;

import jakarta.validation.Valid;

@RestController
@Validated
public class UserController {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    public UserController(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    @GetMapping("/hello")
    @RequireRole(Role.ROLE_ADMIN)
    public ResponseEntity<?> hello() {
        return ResponseEntity.ok("hello from user-service");
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegistrationRequest req) {
        try {
            AuthResponse created = userService.register(req);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalArgumentException e) {
            // return 409 for username/email taken
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest req) {
        var authOpt = userService.authenticate(req);
        if (authOpt.isPresent()) {
            return ResponseEntity.ok(authOpt.get());
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("invalid_credentials");
    }
}
