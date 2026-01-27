package pl.allegro.agh.budgetManagement.user.service;

import org.springframework.stereotype.Service;
import pl.allegro.agh.budgetManagement.user.dto.AuthResponse;
import pl.allegro.agh.budgetManagement.user.dto.LoginRequest;
import pl.allegro.agh.budgetManagement.user.dto.RegistrationRequest;
import pl.allegro.agh.budgetManagement.user.model.PasswordUtil;
import pl.allegro.agh.budgetManagement.user.model.User;
import pl.allegro.agh.budgetManagement.user.repository.UserRepository;
import pl.allegro.agh.budgetManagement.user.security.JwtUtil;

import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    public UserService(UserRepository userRepository, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    public AuthResponse register(RegistrationRequest req) {
        if (userRepository.existsByUsername(req.getUsername())) {
            throw new IllegalArgumentException("username_taken");
        }
        if (userRepository.existsByEmail(req.getEmail())) {
            throw new IllegalArgumentException("email_taken");
        }

        User user = new User();
        user.setUsername(req.getUsername());
        user.setPassword(req.getPassword()); // will be hashed by entity @PrePersist
        user.setEmail(req.getEmail());
        user.setNickname(req.getNickname());
        if (req.getRoles() != null && !req.getRoles().isEmpty()) {
            user.setRoles(req.getRoles());
        }

        User saved = userRepository.save(user);
        String token = jwtUtil.generateToken(saved);
        return new AuthResponse(saved, token);
    }

    public Optional<AuthResponse> authenticate(LoginRequest req) {
        Optional<User> u = userRepository.findByUsername(req.getUsername());
        if (u.isEmpty()) return Optional.empty();
        User user = u.get();
        if (PasswordUtil.matches(req.getPassword(), user.getPassword())) {
            String token = jwtUtil.generateToken(user);
            return Optional.of(new AuthResponse(user, token));
        }
        return Optional.empty();
    }
}

