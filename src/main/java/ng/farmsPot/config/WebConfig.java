package ng.farmsPot.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final AdminAuthInterceptor adminAuthInterceptor;
    private final FarmerAuthInterceptor farmerAuthInterceptor;
    private final BuyerAuthInterceptor buyerAuthInterceptor;

    public WebConfig(AdminAuthInterceptor adminAuthInterceptor,
                     FarmerAuthInterceptor farmerAuthInterceptor,
                     BuyerAuthInterceptor buyerAuthInterceptor) {
        this.adminAuthInterceptor = adminAuthInterceptor;
        this.farmerAuthInterceptor = farmerAuthInterceptor;
        this.buyerAuthInterceptor = buyerAuthInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // Everything under /api/admin (loans, farmers, marketplace), plus releasing loan funds, is admin-only.
        registry.addInterceptor(adminAuthInterceptor)
                .addPathPatterns("/api/admin/**", "/api/loans/disbursedLoan/**");

        // Farmer actions need a valid farmer login. Register, login, logout and health stay open.
        registry.addInterceptor(farmerAuthInterceptor)
                .addPathPatterns(
                        "/api/farmer/*/dashboard",
                        "/api/farmer/*/profile",
                        "/api/loans/applyLoan",
                        "/api/loans/recordEscrowPayment/**",
                        "/api/b2b/farmer/**");

        // Everything a buyer does after signing in. Buyer register and login stay open.
        registry.addInterceptor(buyerAuthInterceptor)
                .addPathPatterns("/api/b2b/buyer/**");
    }
}