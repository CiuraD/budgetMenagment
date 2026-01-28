package pl.allegro.agh.budgetManagement.user.security;

import org.springframework.security.core.context.SecurityContextHolder;

import java.util.UUID;

public final class SecurityUtils {
    private SecurityUtils() {}
    public static UUID currentUserId() {
        return UUID.fromString(
                SecurityContextHolder.getContext()
                        .getAuthentication()
                        .getName()
        );
    }
}
