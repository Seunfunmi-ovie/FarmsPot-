package ng.farmsPot.controllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ng.farmsPot.b2b.data.models.Buyer;
import ng.farmsPot.b2b.data.repositories.BuyerRepository;
import ng.farmsPot.services.TokenService;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;
import java.util.Optional;

/** Requires a valid buyer login token and records who the buyer is (attribute "authBuyerId"). */
@Component
public class BuyerAuthInterceptor implements HandlerInterceptor {

    private final TokenService tokenService;
    private final BuyerRepository buyerRepository;

    public BuyerAuthInterceptor(TokenService tokenService, BuyerRepository buyerRepository) {
        this.tokenService = tokenService;
        this.buyerRepository = buyerRepository;
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

        Optional<Integer> buyerId = tokenService.verifyBuyer(header.substring(7).trim());
        if (buyerId.isEmpty()) {
            return reject(response, "Your session has expired. Please sign in again.");
        }

        Optional<Buyer> buyer = buyerRepository.findById(buyerId.get());
        if (buyer.isEmpty()) {
            return reject(response, "Your session has expired. Please sign in again.");
        }

        request.setAttribute("authBuyerId", buyer.get().getId());
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
