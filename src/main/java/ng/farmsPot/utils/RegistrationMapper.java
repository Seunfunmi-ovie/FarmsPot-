package ng.farmsPot.utils;

import ng.farmsPot.data.models.Farmer;
import ng.farmsPot.dtos.requests.RegisterFarmerRequest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class RegistrationMapper {
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public static void registrationMapper(RegisterFarmerRequest registerFarmerRequest, Farmer farmer, String encodedPassword) {
        farmer.setFullName(registerFarmerRequest.getFullName());
        farmer.setUserName(registerFarmerRequest.getUserName());
        farmer.setPassword(encodedPassword);
        farmer.setPhoneNumber(registerFarmerRequest.getPhoneNumber());
        farmer.setEmail(registerFarmerRequest.getEmail());
        farmer.setFarmLocation(registerFarmerRequest.getFarmLocation());
        farmer.setFarmSizeHectares(registerFarmerRequest.getFarmSizeHectares());
        farmer.setHistoricAverageYieldKg(registerFarmerRequest.getHistoricAverageYieldKg());
    }

}
