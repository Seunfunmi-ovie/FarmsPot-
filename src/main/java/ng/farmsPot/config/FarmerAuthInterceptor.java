package ng.farmsPot.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ng.farmsPot.data.models.Farmer;
import ng.farmsPot.data.repositories.FarmersRepository;
import ng.farmsPot.services.TokenService;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;
import java.util.Optional;

/**
 * Requires a valid login token on farmer endpoints and records who the caller is
 * (request attribute "authFarmerId"). Controllers then check the caller owns the data.
 */
@Component
public class FarmerAuthInterceptor implements HandlerInterceptor {

    private final TokenService tokenService;
    private final FarmersRepository farmersRepository;

    public FarmerAuthInterceptor(TokenService tokenService, FarmersRepository farmersRepository) {
        this.tokenService = tokenService;
        this.farmersRepository = farmersRepository;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            return reject(response, "Please sign in to continue.");
        }

        Optional<Integer> farmerId = tokenService.verify(header.substring(7).trim());
        if (farmerId.isEmpty()) {
            return reject(response, "Your session has expired. Please sign in again.");
        }

        Optional<Farmer> account = farmersRepository.findById(farmerId.get());
        if (account.isEmpty() || Boolean.TRUE.equals(account.get().getSuspended())) {
            return reject(response, "Your session has expired. Please sign in again.");
        }

        request.setAttribute("authFarmerId", account.get().getId());
        return true;
    }

    private boolean reject(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write("{\"message\":\"" + message + "\"}");
        return false;
    }
}
