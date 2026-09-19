package ng.farmsPot.dtos.requests;

import lombok.Data;

@Data
public class LoginFarmerRequest {
    private String userName;
    private String password;
}
