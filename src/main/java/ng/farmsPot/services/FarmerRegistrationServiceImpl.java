package ng.farmsPot.services;

import ng.farmsPot.data.models.Farmer;
import ng.farmsPot.data.repositories.FarmersRepository;
import ng.farmsPot.dtos.requests.LoginFarmerRequest;
import ng.farmsPot.dtos.requests.LogoutFarmersRequest;
import ng.farmsPot.dtos.requests.RegisterFarmerRequest;
import ng.farmsPot.dtos.responses.LoginFarmerResponses;
import ng.farmsPot.dtos.responses.LogoutFarmerResponse;
import ng.farmsPot.dtos.responses.RegisterFarmerResponses;

import ng.farmsPot.utils.RegistrationMapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;



@Service
public class FarmerRegistrationServiceImpl implements FarmerRegistrationServices{
   private final FarmersRepository farmersRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final EmailService emailService;

    public FarmerRegistrationServiceImpl(FarmersRepository farmersRepository, EmailService emailService) {
        this.farmersRepository = farmersRepository;
        this.emailService = emailService;
    }

    @Override
    public RegisterFarmerResponses registerFarmer(RegisterFarmerRequest registerFarmerRequest) {
        Optional<Farmer> existingFarmer = farmersRepository.findByPhoneNumber(registerFarmerRequest.getPhoneNumber());
        if (existingFarmer.isPresent()) {
            throw new IllegalArgumentException("Phone number already in use");
        }
        Farmer farmer = new Farmer();
        String encodedPassword = passwordEncoder.encode(registerFarmerRequest.getPassword());
        RegistrationMapper.registrationMapper(registerFarmerRequest, farmer,encodedPassword);

        farmersRepository.save(farmer);
        emailService.registrationConfirmationEmail(farmer.getEmail(), farmer.getFullName());


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

        LoginFarmerResponses responses = new LoginFarmerResponses();
        responses.setUserName(request.getUserName());
        responses.setMessage("Login Successful...");


        return  responses;


    }


    @Override
    public LogoutFarmerResponse logoutFarmer(LogoutFarmersRequest request) {


        LogoutFarmerResponse responses = new LogoutFarmerResponse();
        responses.setMessage("Logout Successful...");

        return responses;
    }
}
