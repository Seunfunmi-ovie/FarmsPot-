package ng.farmsPot.dtos.responses;

import lombok.Data;

@Data
public class UpdateFarmerProfileResponse {
    private int farmerId;
    private String fullName;
    private String email;
    private String phoneNumber;
    private String farmLocation;
    private double farmSizeHectares;
    private double historicAverageYieldKg;
    private String message;
}
