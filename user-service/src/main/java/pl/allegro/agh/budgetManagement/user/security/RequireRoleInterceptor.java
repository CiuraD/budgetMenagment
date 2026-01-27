package pl.allegro.agh.budgetManagement.user.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import pl.allegro.agh.budgetManagement.user.model.Role;

import java.lang.reflect.Method;

@Component
public class RequireRoleInterceptor implements HandlerInterceptor {
    private final JwtUtil jwtUtil;

    public RequireRoleInterceptor(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        HandlerMethod hm = (HandlerMethod) handler;
        Method method = hm.getMethod();

        RequireRole ann = method.getAnnotation(RequireRole.class);
        if (ann == null) {
            ann = hm.getBeanType().getAnnotation(RequireRole.class);
        }

        if (ann == null) {
            return true; // no role required
        }

        Role required = ann.value();

        String auth = request.getHeader("Authorization");
        if (auth == null || !auth.startsWith("Bearer ")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("missing_or_invalid_authorization_header");
            return false;
        }
        String token = auth.substring("Bearer ".length());
        boolean ok = jwtUtil.hasRole(token, required);
        if (!ok) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write("insufficient_role");
            return false;
        }
        return true;
    }
}

