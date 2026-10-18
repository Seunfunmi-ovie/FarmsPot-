package ng.farmsPot.dtos.responses;

import lombok.Data;
import ng.farmsPot.data.models.LoanStatus;

import java.time.LocalDateTime;

@Data
public class LoanSummaryResponse {
    private int loanId;
    private double requestedAmount;
    private double amountRepaid;
    private LoanStatus status;
    private LocalDateTime disbursementDate;
}