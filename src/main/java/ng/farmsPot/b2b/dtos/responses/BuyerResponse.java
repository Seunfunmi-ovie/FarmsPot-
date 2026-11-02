package ng.farmsPot.b2b.dtos.responses;

import lombok.Data;
import ng.farmsPot.b2b.data.models.BuyerStatus;

import java.time.LocalDateTime;

@Data
public class BuyerResponse {
    private int buyerId;
    private String companyName;
    private String contactName;
    private String email;
    private String phoneNumber;
    private String country;
    private String businessRegNumber;
    private BuyerStatus status;
    private String verificationNote;
    private LocalDateTime createdAt;
}
