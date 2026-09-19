package ng.farmsPot.dtos.requests;

import jakarta.validation.constraints.Positive;
import lombok.Data;


import ng.farmsPot.data.models.PaymentMethod;

@Data
public class LoanApplicationRequest {

    private int farmerId;
    @Positive
    private double requestedAmount;

    private PaymentMethod paymentMethod;

    private boolean escrowEnabled;

}
