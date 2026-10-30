package ng.farmsPot.services;

import ng.farmsPot.dtos.requests.LoginFarmerRequest;
import ng.farmsPot.dtos.requests.LogoutFarmersRequest;
import ng.farmsPot.dtos.requests.RegisterFarmerRequest;
import ng.farmsPot.dtos.requests.UpdateFarmerProfileRequest;
import ng.farmsPot.dtos.responses.LoginFarmerResponses;
import ng.farmsPot.dtos.responses.LogoutFarmerResponse;
import ng.farmsPot.dtos.responses.RegisterFarmerResponses;
import ng.farmsPot.dtos.responses.UpdateFarmerProfileResponse;

public interface FarmerRegistrationServices {
    RegisterFarmerResponses registerFarmer(RegisterFarmerRequest registerFarmerRequest);
    LoginFarmerResponses loginFarmer(LoginFarmerRequest request);
    LogoutFarmerResponse logoutFarmer(LogoutFarmersRequest request);
    UpdateFarmerProfileResponse updateProfile(int farmerId, UpdateFarmerProfileRequest request);
}

