package ng.farmsPot.b2b.dtos.requests;

import lombok.Data;

@Data
public class BuyerLoginRequest {
    private String email;
    private String password;
}
