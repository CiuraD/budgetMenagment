package pl.allegro.agh.budgetManagement.user.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import pl.allegro.agh.budgetManagement.user.dto.AuthResponse;
import pl.allegro.agh.budgetManagement.user.dto.LoginRequest;
import pl.allegro.agh.budgetManagement.user.dto.RegistrationRequest;
import pl.allegro.agh.budgetManagement.user.model.Role;
import pl.allegro.agh.budgetManagement.user.model.User;
import pl.allegro.agh.budgetManagement.user.repository.UserRepository;
import pl.allegro.agh.budgetManagement.user.security.JwtUtil;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    UserRepository userRepository;

    @Mock
    JwtUtil jwtUtil;

    @InjectMocks
    UserService userService;

    @Test
    void register_success_generatesTokenAndReturnsAuthResponse() {
        RegistrationRequest req = new RegistrationRequest();
        req.setUsername("john");
        req.setPassword("secret");
        req.setEmail("john@example.com");
        req.setNickname("Johnny");

        when(userRepository.existsByUsername("john")).thenReturn(false);
        when(userRepository.existsByEmail("john@example.com")).thenReturn(false);

        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setUsername("john");
        savedUser.setEmail("john@example.com");

        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(jwtUtil.generateToken(savedUser)).thenReturn("token123");

        AuthResponse resp = userService.register(req);

        assertNotNull(resp);
        assertEquals(savedUser.getId(), resp.getId());
        assertEquals(savedUser.getUsername(), resp.getUsername());
        assertEquals("token123", resp.getToken());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void register_fails_whenUsernameTaken() {
        RegistrationRequest req = new RegistrationRequest();
        req.setUsername("john");
        req.setPassword("secret");
        req.setEmail("john@example.com");

        when(userRepository.existsByUsername("john")).thenReturn(true);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> userService.register(req));
        assertEquals("username_taken", ex.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    void register_fails_whenEmailTaken() {
        RegistrationRequest req = new RegistrationRequest();
        req.setUsername("john");
        req.setPassword("secret");
        req.setEmail("john@example.com");

        when(userRepository.existsByUsername("john")).thenReturn(false);
        when(userRepository.existsByEmail("john@example.com")).thenReturn(true);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> userService.register(req));
        assertEquals("email_taken", ex.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    void authenticate_returnsEmpty_whenUserNotFound() {
        LoginRequest req = new LoginRequest();
        req.setUsername("nosuch");
        req.setPassword("pw");

        when(userRepository.findByUsername("nosuch")).thenReturn(Optional.empty());

        Optional<AuthResponse> result = userService.authenticate(req);
        assertTrue(result.isEmpty());
    }

    @Test
    void authenticate_returnsEmpty_whenPasswordMismatch() {
        LoginRequest req = new LoginRequest();
        req.setUsername("john");
        req.setPassword("wrong");

        User user = new User();
        user.setUsername("john");
        user.setPassword(new BCryptPasswordEncoder().encode("secret")); // encoded for a different raw

        when(userRepository.findByUsername("john")).thenReturn(Optional.of(user));

        Optional<AuthResponse> result = userService.authenticate(req);
        assertTrue(result.isEmpty());
    }

    @Test
    void authenticate_success_returnsAuthResponse() {
        LoginRequest req = new LoginRequest();
        req.setUsername("john");
        req.setPassword("secret");

        User user = new User();
        user.setId(1L);
        user.setUsername("john");
        // store encoded password using BCryptPasswordEncoder to keep realism
        user.setPassword(new BCryptPasswordEncoder().encode("secret"));

        when(userRepository.findByUsername("john")).thenReturn(Optional.of(user));
        when(jwtUtil.generateToken(user)).thenReturn("tok");

        Optional<AuthResponse> result = userService.authenticate(req);
        assertTrue(result.isPresent());
        assertEquals("tok", result.get().getToken());
        assertEquals(user.getUsername(), result.get().getUsername());
    }

    @Test
    void register_allows_specified_roles() {
        RegistrationRequest req = new RegistrationRequest();
        req.setUsername("alice");
        req.setPassword("pw");
        req.setEmail("a@a.com");
        req.setRoles(Set.of(Role.ROLE_ADMIN));

        when(userRepository.existsByUsername("alice")).thenReturn(false);
        when(userRepository.existsByEmail("a@a.com")).thenReturn(false);

        User saved = new User();
        saved.setId(2L);
        saved.setUsername("alice");
        saved.setRoles(Set.of(Role.ROLE_ADMIN));

        when(userRepository.save(any(User.class))).thenReturn(saved);
        when(jwtUtil.generateToken(saved)).thenReturn("t2");

        AuthResponse resp = userService.register(req);
        assertEquals(Set.of(Role.ROLE_ADMIN), resp.getRoles());
    }
}
