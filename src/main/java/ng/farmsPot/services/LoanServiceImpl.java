package ng.farmsPot.services;

import ng.farmsPot.data.models.Farmer;
import ng.farmsPot.data.models.Loan;
import ng.farmsPot.data.models.LoanStatus;
import ng.farmsPot.data.repositories.FarmersRepository;
import ng.farmsPot.data.repositories.LoanRepository;
import ng.farmsPot.dtos.requests.LoanApplicationRequest;
import ng.farmsPot.dtos.responses.LoanApplicationResponse;
import ng.farmsPot.utils.LoanMapper;
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
        LoanMapper.loanMapper(request, loan, farmer, creditScore);

        if (creditScore < 40) {
            loan.setStatus(LoanStatus.REJECTED);
            Loan savedRejectedLoan = loanRepository.save(loan);

            LoanApplicationResponse rejectedResponse = new LoanApplicationResponse();
            LoanMapper.rejectedResponseMapper (rejectedResponse, savedRejectedLoan, creditScore);

            return rejectedResponse;
        }

        loan.setStatus(LoanStatus.PENDING);
        Loan savedLoan = loanRepository.save(loan);

        LoanApplicationResponse response = new LoanApplicationResponse();
        LoanMapper.loanApplicationMapper(response, savedLoan, creditScore);

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