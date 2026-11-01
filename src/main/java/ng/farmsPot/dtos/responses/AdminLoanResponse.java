package ng.farmsPot.dtos.responses;

import lombok.Data;
import ng.farmsPot.data.models.LoanStatus;
import ng.farmsPot.data.models.PaymentMethod;

import java.time.LocalDateTime;

@Data
public class AdminLoanResponse {
    private int loanId;
    private int farmerId;
    private String farmerName;
    private double requestedAmount;
    private Integer creditScore;
    private LoanStatus status;
    private double amountRepaid;
    private LocalDateTime disbursementDate;
    private PaymentMethod paymentMethod;
    private String reviewNote;
}
