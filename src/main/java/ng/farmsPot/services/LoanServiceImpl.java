package ng.farmsPot.services;

import ng.farmsPot.data.models.Farmer;
import ng.farmsPot.data.models.Loan;
import ng.farmsPot.data.models.LoanStatus;
import ng.farmsPot.data.repositories.FarmersRepository;
import ng.farmsPot.data.repositories.LoanRepository;
import ng.farmsPot.dtos.requests.LoanApplicationRequest;
import ng.farmsPot.dtos.responses.LoanApplicationResponse;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class LoanServiceImpl implements LoanService {

    private final LoanRepository loanRepository;
    private final FarmersRepository farmersRepository;
    private final CreditScoringService creditScoringService;

    public LoanServiceImpl(LoanRepository loanRepository, FarmersRepository farmersRepository, CreditScoringService creditScoringService) {
        this.loanRepository = loanRepository;
        this.farmersRepository = farmersRepository;
        this.creditScoringService = creditScoringService;
    }

    @Override
    public LoanApplicationResponse applyLoan(LoanApplicationRequest request) {
        Optional<Farmer> farmerOptional = farmersRepository.findById(request.getFarmerId());
        if (farmerOptional.isEmpty()) {
            throw new IllegalArgumentException("No Farmer found with id " + request.getFarmerId());
        }

        Farmer farmer = farmerOptional.get();
        int creditScore = creditScoringService.creditScore(farmer);

        Loan loan = new Loan();
        loan.setFarmer(farmer);
        loan.setRequestedAmount(request.getRequestedAmount());
        loan.setPaymentMethod(request.getPaymentMethod());
        loan.setEscrowEnabled(request.isEscrowEnabled());
        loan.setCreditScore(creditScore);

        if (creditScore < 40) {
            loan.setStatus(LoanStatus.REJECTED);
            Loan savedRejectedLoan = loanRepository.save(loan);

            LoanApplicationResponse rejectedResponse = new LoanApplicationResponse();
            rejectedResponse.setLoanId(savedRejectedLoan.getId());
            rejectedResponse.setLoanStatus(savedRejectedLoan.getStatus());
            rejectedResponse.setCreditScore(creditScore);
            rejectedResponse.setRequestedAmount(savedRejectedLoan.getRequestedAmount());
            rejectedResponse.setMessage("Loan application rejected: credit score too low");

            return rejectedResponse;
        }

        loan.setStatus(LoanStatus.PENDING);
        Loan savedLoan = loanRepository.save(loan);

        LoanApplicationResponse response = new LoanApplicationResponse();
        response.setLoanId(savedLoan.getId());
        response.setLoanStatus(savedLoan.getStatus());
        response.setCreditScore(creditScore);
        response.setRequestedAmount(savedLoan.getRequestedAmount());
        response.setMessage("Loan application submitted successfully");

        return response;
    }

    @Override
    public LoanApplicationResponse disburseLoan(int loanId) {
        return null;
    }

    @Override
    public LoanApplicationResponse recordEscrowPayment(int loanId, double amount) {
        return null;
    }
}