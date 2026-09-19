package ng.farmsPot.dtos.requests;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RegisterFarmerRequest {
    private String fullName;
    private String email;
    private String userName;
    private String phoneNumber;
    private String password;
    private double farmSizeHectares;
    private double historicAverageYieldKg;
    private String farmLocation;


}
