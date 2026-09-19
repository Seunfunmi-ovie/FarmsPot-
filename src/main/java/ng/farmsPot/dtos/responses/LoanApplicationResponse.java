package ng.farmsPot.dtos.responses;

import lombok.Data;
import ng.farmsPot.data.models.LoanStatus;
@Data
public class LoanApplicationResponse {

    private int loanId;

    private LoanStatus loanStatus;

    private Integer creditScore;

    private double finalMaxAmount;

    private double requestedAmount;

    private String message;

}