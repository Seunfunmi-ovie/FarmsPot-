package ng.farmsPot.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final AdminAuthInterceptor adminAuthInterceptor;
    private final FarmerAuthInterceptor farmerAuthInterceptor;

    public WebConfig(AdminAuthInterceptor adminAuthInterceptor, FarmerAuthInterceptor farmerAuthInterceptor) {
        this.adminAuthInterceptor = adminAuthInterceptor;
        this.farmerAuthInterceptor = farmerAuthInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // Everything under /api/admin, plus releasing loan funds, is admin-only.
        registry.addInterceptor(adminAuthInterceptor)
                .addPathPatterns("/api/admin/**", "/api/loans/disbursedLoan/**");

        // Farmer actions need a valid login. Register, login, logout and health stay open.
        registry.addInterceptor(farmerAuthInterceptor)
                .addPathPatterns(
                        "/api/farmer/*/dashboard",
                        "/api/farmer/*/profile",
                        "/api/loans/applyLoan",
                        "/api/loans/recordEscrowPayment/**");
    }
}
