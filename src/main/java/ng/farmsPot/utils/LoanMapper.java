package ng.farmsPot.utils;

import ng.farmsPot.data.models.Farmer;
import ng.farmsPot.data.models.Loan;
import ng.farmsPot.dtos.requests.LoanApplicationRequest;
import ng.farmsPot.dtos.responses.LoanApplicationResponse;

public class LoanMapper {

    public static void loanMapper(LoanApplicationRequest request, Loan loan, Farmer farmer, Integer creditScore) {
        loan.setFarmer(farmer);
        loan.setRequestedAmount(request.getRequestedAmount());
        loan.setPaymentMethod(request.getPaymentMethod());
        loan.setEscrowEnabled(request.isEscrowEnabled());
        loan.setCreditScore(creditScore);
    }

    public static void rejectedResponseMapper(LoanApplicationResponse rejectedResponse, Loan savedRejectedLoan, Integer creditScore) {
        rejectedResponse.setLoanId(savedRejectedLoan.getId());
        rejectedResponse.setLoanStatus(savedRejectedLoan.getStatus());
        rejectedResponse.setCreditScore(creditScore);
        rejectedResponse.setRequestedAmount(savedRejectedLoan.getRequestedAmount());
        rejectedResponse.setMessage("Loan application rejected: credit score too low");
    }

    public static void loanApplicationMapper(LoanApplicationResponse response, Loan savedLoan, Integer creditScore) {
        response.setLoanId(savedLoan.getId());
        response.setLoanStatus(savedLoan.getStatus());
        response.setCreditScore(creditScore);
        response.setRequestedAmount(savedLoan.getRequestedAmount());
        response.setMessage("Loan application submitted successfully");
    }

    public static void disqualifiedResponseMapper(LoanApplicationResponse response, Loan savedLoan) {
        response.setLoanId(savedLoan.getId());
        response.setLoanStatus(savedLoan.getStatus());
        response.setCreditScore(null);
        response.setRequestedAmount(savedLoan.getRequestedAmount());
        response.setMessage("Loan application rejected: recent loan default");
    }

    public static void exceededAmountMapper(LoanApplicationResponse response, Loan savedLoan, Integer creditScore) {
        response.setLoanId(savedLoan.getId());
        response.setLoanStatus(savedLoan.getStatus());
        response.setCreditScore(creditScore);
        response.setRequestedAmount(savedLoan.getRequestedAmount());
        response.setMessage("Loan application rejected: Amount requested is above Loan Limit");
    }

    public static void disbursedLoanMapper(LoanApplicationResponse response, Loan savedLoan, Integer creditScore) {
        response.setLoanId(savedLoan.getId());
        response.setLoanStatus(savedLoan.getStatus());
        response.setCreditScore(creditScore);
        response.setRequestedAmount(savedLoan.getRequestedAmount());
        response.setMessage("Loan disbursed successfully");
    }

    public static void escrowPaymentResponseMapper(LoanApplicationResponse response, Loan savedLoan, double appliedAmount) {
        response.setLoanId(savedLoan.getId());
        response.setLoanStatus(savedLoan.getStatus());
        response.setRequestedAmount(savedLoan.getRequestedAmount());
        response.setMessage("Payment of loan" + appliedAmount + " has been recorded against loan");
    }
}
