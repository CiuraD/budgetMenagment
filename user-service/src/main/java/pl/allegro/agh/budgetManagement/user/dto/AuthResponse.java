package pl.allegro.agh.budgetManagement.user.dto;

import pl.allegro.agh.budgetManagement.user.model.Role;
import pl.allegro.agh.budgetManagement.user.model.User;

import java.util.Set;

public class AuthResponse {
    private Long id;
    private String username;
    private String email;
    private String nickname;
    private Set<Role> roles;
    private String token;

    public AuthResponse() {}

    public AuthResponse(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.nickname = user.getNickname();
        this.roles = user.getRoles();
    }

    public AuthResponse(User user, String token) {
        this(user);
        this.token = token;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }
    public Set<Role> getRoles() { return roles; }
    public void setRoles(Set<Role> roles) { this.roles = roles; }
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
}

