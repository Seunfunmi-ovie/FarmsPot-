package ng.farmsPot.services;

import ng.farmsPot.data.models.Farmer;
import ng.farmsPot.data.models.Loan;
import ng.farmsPot.data.models.LoanStatus;
import ng.farmsPot.data.repositories.FarmersRepository;
import ng.farmsPot.data.repositories.LoanRepository;
import ng.farmsPot.dtos.requests.LoanApplicationRequest;
import ng.farmsPot.dtos.responses.LoanApplicationResponse;
import ng.farmsPot.utils.LoanMapper;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Service
public class LoanServiceImpl implements LoanService {

    private final LoanRepository loanRepository;
    private final FarmersRepository farmersRepository;
    private final CreditScoringService creditScoringService;
    private static final Logger logger = LoggerFactory.getLogger(LoanServiceImpl.class);
    private final EmailService emailService;

    public LoanServiceImpl(LoanRepository loanRepository, FarmersRepository farmersRepository, CreditScoringService creditScoringService, EmailService emailService) {
        this.loanRepository = loanRepository;
        this.farmersRepository = farmersRepository;
        this.creditScoringService = creditScoringService;

        this.emailService = emailService;
    }

    @Override
    public LoanApplicationResponse applyLoan(LoanApplicationRequest request) {
        Optional<Farmer> farmerOptional = farmersRepository.findById(request.getFarmerId());
        if (farmerOptional.isEmpty()) {
            throw new IllegalArgumentException("No Farmer found with id " + request.getFarmerId());
        }

        Farmer farmer = farmerOptional.get();
        List<Loan> farmersLoan = loanRepository.findByFarmerIdOrderByIdAsc(request.getFarmerId());

        logger.info("The logic here is that at start point every farmer start at -1 if the have never defaulted a loan before then move to 0 at the point if default, then the counter count the number of times they fully paid after the defaulted period after 2 times then they csn acqure another loan ");
        int rapidSinceDefault = -1;
        int fullyRepaidCount = 0;
        for(Loan pastLoan :  farmersLoan) {
          if(pastLoan.getStatus() == LoanStatus.DEFAULTED){
              rapidSinceDefault = 0;
          }
          if(pastLoan.getStatus() == LoanStatus.FULLY_REPAID && rapidSinceDefault != -1){
              rapidSinceDefault += 1;
          }
          if(pastLoan.getStatus() == LoanStatus.FULLY_REPAID){
              fullyRepaidCount += 1;
          }
          if(pastLoan.getStatus() == LoanStatus.DEFAULTED){
              fullyRepaidCount = 0;
          }
        }

      
        Loan loan = new Loan();
        LoanMapper.loanMapper(request, loan, farmer, null);
        if(rapidSinceDefault != -1 && rapidSinceDefault < 2){
            loan.setStatus(LoanStatus.REJECTED);
            Loan savedDisqualifiedLoan = loanRepository.save(loan);

            LoanApplicationResponse disqualifiedResponse = new LoanApplicationResponse();
            LoanMapper.disqualifiedResponseMapper(disqualifiedResponse, savedDisqualifiedLoan);

            return disqualifiedResponse;
        }


        int creditScore = creditScoringService.creditScore(farmer);
        LoanMapper.loanMapper(request, loan, farmer, creditScore);
        if (creditScore < 40) {
            loan.setStatus(LoanStatus.REJECTED);
            Loan savedRejectedLoan = loanRepository.save(loan);

            LoanApplicationResponse rejectedResponse = new LoanApplicationResponse();
            LoanMapper.rejectedResponseMapper (rejectedResponse, savedRejectedLoan, creditScore);

            return rejectedResponse;
        }
        double baseMaxAmount = (farmer.getFarmSizeHectares() * 50000) + (farmer.getHistoricAverageYieldKg() * 100);
        double repaymentMultiplier = 1.0 + (0.20 * fullyRepaidCount);


        double finalMaxAmount =  baseMaxAmount * repaymentMultiplier;
        if (request.getRequestedAmount() > finalMaxAmount) {
            loan.setStatus(LoanStatus.REJECTED);
            Loan exceededLoan = loanRepository.save(loan);
            LoanApplicationResponse exceededResponse = new LoanApplicationResponse();
            LoanMapper.exceededAmountMapper(exceededResponse,exceededLoan, creditScore);
            return exceededResponse;
        }

        loan.setStatus(LoanStatus.PENDING);
        Loan savedLoan = loanRepository.save(loan);

        LoanApplicationResponse response = new LoanApplicationResponse();
        LoanMapper.loanApplicationMapper(response, savedLoan, creditScore);


        return response;
    }




    @Override
    public LoanApplicationResponse disburseLoan(int loanId) {
    Optional<Loan> loanOptional = loanRepository.findById(loanId);
    if(loanOptional.isEmpty()){
        throw new IllegalArgumentException("No Loan found with id " + loanId);
    }

        Loan loan = loanOptional.get();
        if(loan.getStatus() != LoanStatus.PENDING){
            throw new IllegalStateException("Loan is not in a pending state and cannot be disbursed. Current status: " + loan.getStatus());

        }
        loan.setStatus(LoanStatus.APPROVED);
        loan.setDisbursementDate(LocalDateTime.now());
        Loan approvedLoan = loanRepository.save(loan);

        LoanApplicationResponse response = new LoanApplicationResponse();
        LoanMapper.disbursedLoanMapper(response,approvedLoan, approvedLoan.getCreditScore());

        return response;
    }

    @Override
    public LoanApplicationResponse recordEscrowPayment(int loanId, double amount) {
        return null;
    }


    private long getDaysSinceDisbursement(Loan loan) {
    return ChronoUnit.DAYS.between(loan.getDisbursementDate(),LocalDateTime.now());
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void checkLoanDefaults(){
        List<Loan> checkLoan = loanRepository.findByStatus(LoanStatus.APPROVED);
        for(Loan loan : checkLoan){
          long daysPassed = getDaysSinceDisbursement(loan);
          if(daysPassed == 31){
              emailService.loanDueWarningEmail(loan.getFarmer().getEmail(), loan.getFarmer().getFullName());


          }
          if(daysPassed >= 33){
              loan.setStatus(LoanStatus.DEFAULTED);
            Loan defaultedLoan = loanRepository.save(loan);

          }
        }
    }

    }