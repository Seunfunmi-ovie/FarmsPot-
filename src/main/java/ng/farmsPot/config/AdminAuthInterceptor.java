package ng.farmsPot.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ng.farmsPot.data.models.Farmer;
import ng.farmsPot.data.models.Role;
import ng.farmsPot.data.repositories.FarmersRepository;
import ng.farmsPot.services.TokenService;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;
import java.util.Optional;

/** Lets a request through only if it carries a valid token that belongs to an active admin. */
@Component
public class AdminAuthInterceptor implements HandlerInterceptor {

    private final TokenService tokenService;
    private final FarmersRepository farmersRepository;

    public AdminAuthInterceptor(TokenService tokenService, FarmersRepository farmersRepository) {
        this.tokenService = tokenService;
        this.farmersRepository = farmersRepository;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        // Browser pre-flight checks carry no token and must pass.
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            return reject(response, HttpServletResponse.SC_UNAUTHORIZED, "Please sign in as an admin to continue.");
        }

        Optional<Integer> farmerId = tokenService.verify(header.substring(7).trim());
        if (farmerId.isEmpty()) {
            return reject(response, HttpServletResponse.SC_UNAUTHORIZED, "Your session has expired. Please sign in again.");
        }

        Optional<Farmer> account = farmersRepository.findById(farmerId.get());
        if (account.isEmpty() || Boolean.TRUE.equals(account.get().getSuspended())) {
            return reject(response, HttpServletResponse.SC_UNAUTHORIZED, "Your session has expired. Please sign in again.");
        }
        if (account.get().getRole() != Role.ADMIN) {
            return reject(response, HttpServletResponse.SC_FORBIDDEN, "Only admins can do this.");
        }

        request.setAttribute("adminId", account.get().getId());
        return true;
    }

    private boolean reject(HttpServletResponse response, int status, String message) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write("{\"message\":\"" + message + "\"}");
        return false;
    }
}
