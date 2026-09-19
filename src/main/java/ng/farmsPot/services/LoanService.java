package ng.farmsPot.services;


import ng.farmsPot.dtos.requests.LoanApplicationRequest;
import ng.farmsPot.dtos.responses.LoanApplicationResponse;

public interface LoanService {


    LoanApplicationResponse applyLoan(LoanApplicationRequest request);

    LoanApplicationResponse disburseLoan(int loanId);

    LoanApplicationResponse recordEscrowPayment(int loanId, double amount);

}
