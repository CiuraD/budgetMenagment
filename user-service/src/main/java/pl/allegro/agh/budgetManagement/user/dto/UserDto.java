package pl.allegro.agh.budgetManagement.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import java.util.Objects;

public class UserDto {

    private Long userId;

    @NotBlank
    private String username;

    @NotBlank
    @Email
    private String email;

    private String displayName;

    @NotNull
    private Instant createdAt;

    public UserDto() {
        this.createdAt = Instant.now();
    }

    public UserDto(Long userId, String username, String email, String displayName, Instant createdAt) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.displayName = displayName;
        this.createdAt = createdAt != null ? createdAt : Instant.now();
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserDto userDto = (UserDto) o;
        return Objects.equals(userId, userDto.userId)
                && Objects.equals(username, userDto.username)
                && Objects.equals(email, userDto.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, username, email);
    }

    @Override
    public String toString() {
        return "UserDto{" +
                "userId=" + userId +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", displayName='" + displayName + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
