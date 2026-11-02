package ng.farmsPot.b2b.services;

import ng.farmsPot.b2b.dtos.requests.BuyerLoginRequest;
import ng.farmsPot.b2b.dtos.responses.BuyerLoginResponse;
import ng.farmsPot.b2b.dtos.requests.BuyerRegisterRequest;
import ng.farmsPot.b2b.dtos.responses.BuyerResponse;
import ng.farmsPot.b2b.data.models.Buyer;
import ng.farmsPot.b2b.data.models.BuyerStatus;
import ng.farmsPot.b2b.data.repositories.BuyerRepository;
import ng.farmsPot.b2b.utils.B2bMapper;
import ng.farmsPot.services.TokenService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class BuyerServiceImpl implements BuyerService {

    private final BuyerRepository buyerRepository;
    private final TokenService tokenService;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public BuyerServiceImpl(BuyerRepository buyerRepository, TokenService tokenService) {
        this.buyerRepository = buyerRepository;
        this.tokenService = tokenService;
    }

    @Transactional
    @Override
    public BuyerResponse register(BuyerRegisterRequest request) {
        String email = request.getEmail().trim().toLowerCase();
        if (buyerRepository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("An account with this email already exists");
        }

        Buyer buyer = new Buyer();
        buyer.setCompanyName(request.getCompanyName().trim());
        buyer.setContactName(request.getContactName().trim());
        buyer.setEmail(email);
        buyer.setPhoneNumber(request.getPhoneNumber().trim());
        buyer.setCountry(request.getCountry().trim());
        buyer.setBusinessRegNumber(request.getBusinessRegNumber().trim());
        buyer.setPassword(passwordEncoder.encode(request.getPassword()));
        buyer.setStatus(BuyerStatus.PENDING);

        return B2bMapper.toBuyer(buyerRepository.save(buyer));
    }

    @Override
    public BuyerLoginResponse login(BuyerLoginRequest request) {
        String email = request.getEmail() == null ? "" : request.getEmail().trim().toLowerCase();
        Optional<Buyer> found = buyerRepository.findByEmail(email);

        // Same message for an unknown email and a wrong password, so accounts can't be probed.
        if (found.isEmpty() || request.getPassword() == null
                || !passwordEncoder.matches(request.getPassword(), found.get().getPassword())) {
            throw new IllegalArgumentException("Incorrect email or password....");
        }

        Buyer buyer = found.get();
        BuyerLoginResponse response = new BuyerLoginResponse();
        response.setBuyerId(buyer.getId());
        response.setCompanyName(buyer.getCompanyName());
        response.setStatus(buyer.getStatus());
        response.setToken(tokenService.createBuyerToken(buyer.getId()));
        response.setMessage("Login Successful...");
        return response;
    }

    @Override
    public BuyerResponse profile(int buyerId) {
        Buyer buyer = buyerRepository.findById(buyerId)
                .orElseThrow(() -> new IllegalArgumentException("No Buyer found with id " + buyerId));
        return B2bMapper.toBuyer(buyer);
    }
}
