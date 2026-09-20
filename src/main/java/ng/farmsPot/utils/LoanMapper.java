package ng.farmsPot.utils;

import ng.farmsPot.data.models.Farmer;
import ng.farmsPot.data.models.Loan;
import ng.farmsPot.dtos.requests.LoanApplicationRequest;
import ng.farmsPot.dtos.responses.LoanApplicationResponse;

public class LoanMapper {

    public static void loanMapper(LoanApplicationRequest request, Loan loan, Farmer farmer, int creditScore) {
        loan.setFarmer(farmer);
        loan.setRequestedAmount(request.getRequestedAmount());
        loan.setPaymentMethod(request.getPaymentMethod());
        loan.setEscrowEnabled(request.isEscrowEnabled());
        loan.setCreditScore(creditScore);
    }

    public static void rejectedResponseMapper(LoanApplicationResponse rejectedResponse, Loan savedRejectedLoan, int creditScore) {
        rejectedResponse.setLoanId(savedRejectedLoan.getId());
        rejectedResponse.setLoanStatus(savedRejectedLoan.getStatus());
        rejectedResponse.setCreditScore(creditScore);
        rejectedResponse.setRequestedAmount(savedRejectedLoan.getRequestedAmount());
        rejectedResponse.setMessage("Loan application rejected: credit score too low");
    }

    public static void loanApplicationMapper(LoanApplicationResponse response, Loan savedLoan, int creditScore) {
        response.setLoanId(savedLoan.getId());
        response.setLoanStatus(savedLoan.getStatus());
        response.setCreditScore(creditScore);
        response.setRequestedAmount(savedLoan.getRequestedAmount());
        response.setMessage("Loan application submitted successfully");
    }
}
