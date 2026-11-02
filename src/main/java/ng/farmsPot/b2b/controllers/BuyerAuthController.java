package ng.farmsPot.b2b.controllers;

import jakarta.validation.Valid;
import ng.farmsPot.b2b.dto.BuyerLoginRequest;
import ng.farmsPot.b2b.dto.BuyerLoginResponse;
import ng.farmsPot.b2b.dto.BuyerRegisterRequest;
import ng.farmsPot.b2b.dto.BuyerResponse;
import ng.farmsPot.b2b.service.BuyerService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Open endpoints: a company registers, then signs in once an admin has verified it. */
@RestController
@RequestMapping("/api/b2b/buyers")
public class BuyerAuthController {

    private final BuyerService buyerService;

    public BuyerAuthController(BuyerService buyerService) {
        this.buyerService = buyerService;
    }

    @PostMapping("/register")
    public BuyerResponse register(@Valid @RequestBody BuyerRegisterRequest request) {
        return buyerService.register(request);
    }

    @PostMapping("/login")
    public BuyerLoginResponse login(@RequestBody BuyerLoginRequest request) {
        return buyerService.login(request);
    }
}
