package ng.farmsPot.controllers;


import ng.farmsPot.dtos.requests.LoginFarmerRequest;
import ng.farmsPot.dtos.requests.LogoutFarmersRequest;
import ng.farmsPot.dtos.requests.RegisterFarmerRequest;
import ng.farmsPot.dtos.responses.LoginFarmerResponses;
import ng.farmsPot.dtos.responses.LogoutFarmerResponse;
import ng.farmsPot.dtos.responses.RegisterFarmerResponses;
import ng.farmsPot.services.FarmerRegistrationServices;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/farmer")
public class FarmersController {
    private final FarmerRegistrationServices farmerRegistrationServices;

    public FarmersController(FarmerRegistrationServices farmerRegistrationServices){
        this.farmerRegistrationServices = farmerRegistrationServices;
    }

    @PostMapping("/register")
    public RegisterFarmerResponses registerFarmer(@RequestBody RegisterFarmerRequest registerFarmerRequest){
        return farmerRegistrationServices.registerFarmer(registerFarmerRequest);
    }

    @PostMapping("/login")
    public LoginFarmerResponses loginFarmer(@RequestBody LoginFarmerRequest request){
        return farmerRegistrationServices.loginFarmer(request);
    }
    @PostMapping("/logout")
    public LogoutFarmerResponse logoutFarmer(@RequestBody LogoutFarmersRequest request){
        return farmerRegistrationServices.logoutFarmer(request);
    }
}
