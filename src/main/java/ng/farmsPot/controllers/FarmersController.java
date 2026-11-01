package ng.farmsPot.controllers;


import jakarta.validation.Valid;
import ng.farmsPot.config.AccessGuard;
import ng.farmsPot.dtos.requests.LoginFarmerRequest;
import ng.farmsPot.dtos.requests.LogoutFarmersRequest;
import ng.farmsPot.dtos.requests.RegisterFarmerRequest;
import ng.farmsPot.dtos.requests.UpdateFarmerProfileRequest;
import ng.farmsPot.dtos.responses.LoginFarmerResponses;
import ng.farmsPot.dtos.responses.LogoutFarmerResponse;
import ng.farmsPot.dtos.responses.RegisterFarmerResponses;
import ng.farmsPot.dtos.responses.UpdateFarmerProfileResponse;
import ng.farmsPot.services.FarmerRegistrationServices;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/farmer")
public class FarmersController {
    private final FarmerRegistrationServices farmerRegistrationServices;
    private final AccessGuard accessGuard;

    public FarmersController(FarmerRegistrationServices farmerRegistrationServices, AccessGuard accessGuard){
        this.farmerRegistrationServices = farmerRegistrationServices;
        this.accessGuard = accessGuard;
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

    @PutMapping("/{farmerId}/profile")
    public UpdateFarmerProfileResponse updateProfile(@RequestAttribute("authFarmerId") int authFarmerId,
                                                     @PathVariable int farmerId,
                                                     @Valid @RequestBody UpdateFarmerProfileRequest request){
        accessGuard.requireSelf(authFarmerId, farmerId);
        return farmerRegistrationServices.updateProfile(farmerId, request);
    }
}