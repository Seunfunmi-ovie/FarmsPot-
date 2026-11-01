package ng.farmsPot.services;


import ng.farmsPot.data.models.Farmer;
import ng.farmsPot.data.models.FarmersAccount;
import ng.farmsPot.data.models.Role;
import ng.farmsPot.data.repositories.FarmersAccountRepository;
import ng.farmsPot.data.repositories.FarmersRepository;
import ng.farmsPot.dtos.requests.LoginFarmerRequest;
import ng.farmsPot.dtos.requests.LogoutFarmersRequest;
import ng.farmsPot.dtos.requests.RegisterFarmerRequest;
import ng.farmsPot.dtos.requests.UpdateFarmerProfileRequest;
import ng.farmsPot.dtos.responses.LoginFarmerResponses;
import ng.farmsPot.dtos.responses.LogoutFarmerResponse;
import ng.farmsPot.dtos.responses.RegisterFarmerResponses;
import ng.farmsPot.dtos.responses.UpdateFarmerProfileResponse;

import ng.farmsPot.utils.ProfileMapper;
import ng.farmsPot.utils.RegistrationMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.Optional;



@Service
public class FarmerRegistrationServiceImpl implements FarmerRegistrationServices{
    private final FarmersRepository farmersRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final EmailService emailService;
    private final FarmersAccountRepository farmersAccountRepository;
    private final TokenService tokenService;
    private static final Logger logger = LoggerFactory.getLogger(FarmerRegistrationServiceImpl.class);

    public FarmerRegistrationServiceImpl(FarmersRepository farmersRepository, EmailService emailService, FarmersAccountRepository farmersAccountRepository, TokenService tokenService) {
        this.farmersRepository = farmersRepository;
        this.emailService = emailService;
        this.farmersAccountRepository = farmersAccountRepository;
        this.tokenService = tokenService;
    }
    @Transactional
    @Override
    public RegisterFarmerResponses registerFarmer(RegisterFarmerRequest registerFarmerRequest) {
        Optional<Farmer> existingFarmer = farmersRepository.findByPhoneNumber(registerFarmerRequest.getPhoneNumber());
        if (existingFarmer.isPresent()) {
            throw new IllegalArgumentException("Phone number already in use");
        }
        Optional<Farmer> existingFarmer1 = farmersRepository.findByUserName(registerFarmerRequest.getUserName());
        if (existingFarmer1.isPresent()) {
            throw new IllegalArgumentException("Username already in use");
        }
        Farmer farmer = new Farmer();
        FarmersAccount farmersAccount = new FarmersAccount();
        String encodedPassword = passwordEncoder.encode(registerFarmerRequest.getPassword());
        RegistrationMapper.registrationMapper(registerFarmerRequest, farmer,encodedPassword);

        Farmer farmer1 = farmersRepository.save(farmer);
        farmersAccount.setFarmer(farmer1);
        farmersAccountRepository.save(farmersAccount);
        try {
            emailService.registrationConfirmationEmail(farmer.getEmail(), farmer.getFullName());
        } catch (Exception exception) {
            logger.warn("Failed to send registration confirmation email to {}", farmer.getEmail(), exception);
        }


        RegisterFarmerResponses responses = new RegisterFarmerResponses();
        responses.setFullName(registerFarmerRequest.getFullName());
        responses.setMessage("Registration Successful...");



        return  responses;

    }


    @Override
    public LoginFarmerResponses loginFarmer(LoginFarmerRequest request) {
        Optional<Farmer> existingFarmer = farmersRepository.findByUserName(request.getUserName());
        if (existingFarmer.isEmpty()) {
            throw new IllegalArgumentException("Incorrect UserName or Password....");
        }
        Farmer farmer = existingFarmer.get();
        if(!passwordEncoder.matches(request.getPassword(), farmer.getPassword())) {
            throw new IllegalArgumentException("Incorrect UserName or Password....");
        }

        if (Boolean.TRUE.equals(farmer.getSuspended())) {
            throw new IllegalArgumentException("This account has been suspended. Please contact FarmsPot support.");
        }

        Role role = farmer.getRole() == null ? Role.FARMER : farmer.getRole();

        LoginFarmerResponses responses = new LoginFarmerResponses();
        responses.setUserName(request.getUserName());
        responses.setMessage("Login Successful...");
        responses.setFarmerId(farmer.getId());
        responses.setRole(role.name());
        responses.setToken(tokenService.createToken(farmer.getId()));


        return  responses;


    }


    @Override
    public LogoutFarmerResponse logoutFarmer(LogoutFarmersRequest request) {


        LogoutFarmerResponse responses = new LogoutFarmerResponse();
        responses.setMessage("Logout Successful...");

        return responses;
    }

    @Transactional
    @Override
    public UpdateFarmerProfileResponse updateProfile(int farmerId, UpdateFarmerProfileRequest request) {
        Farmer farmer = farmersRepository.findById(farmerId)
                .orElseThrow(() -> new IllegalArgumentException("No Farmer found with id " + farmerId));

        String newPhone = request.getPhoneNumber();
        if (newPhone != null && !newPhone.isBlank() && !newPhone.trim().equals(farmer.getPhoneNumber())) {
            Optional<Farmer> owner = farmersRepository.findByPhoneNumber(newPhone.trim());
            if (owner.isPresent() && owner.get().getId() != farmerId) {
                throw new IllegalArgumentException("Phone number already in use");
            }
        }

        ProfileMapper.applyUpdate(request, farmer);
        Farmer saved = farmersRepository.save(farmer);

        return ProfileMapper.toResponse(saved, "Profile updated successfully...");
    }
}