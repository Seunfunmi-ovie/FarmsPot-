package ng.farmsPot.dtos.responses;

import lombok.Data;

@Data
public class LoginFarmerResponses {
    private String userName;
    private String message;
    private int farmerId;
    private String role;
    private String token;

}