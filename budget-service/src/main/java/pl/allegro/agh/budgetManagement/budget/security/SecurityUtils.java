package pl.allegro.agh.budgetManagement.budget.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public final class SecurityUtils {
    private SecurityUtils() {}

    public static Long currentUserId() {
        Authentication auth =
                SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || auth.getName() == null) {
            throw new IllegalStateException("No authenticated user");
        }

        return Long.valueOf(auth.getName());
    }
}
