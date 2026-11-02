package ng.farmsPot.b2b.dtos.responses;

import lombok.Data;
import ng.farmsPot.b2b.model.BuyerStatus;

@Data
public class BuyerLoginResponse {
    private int buyerId;
    private String companyName;
    private BuyerStatus status;
    private String role = "BUYER";
    private String token;
    private String message;
}
