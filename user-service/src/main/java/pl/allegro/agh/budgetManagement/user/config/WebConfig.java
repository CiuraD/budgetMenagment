package pl.allegro.agh.budgetManagement.user.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import pl.allegro.agh.budgetManagement.user.security.RequireRoleInterceptor;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    private final RequireRoleInterceptor requireRoleInterceptor;

    public WebConfig(RequireRoleInterceptor requireRoleInterceptor) {
        this.requireRoleInterceptor = requireRoleInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(requireRoleInterceptor);
    }
}

