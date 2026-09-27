package ng.farmsPot.services;

import ng.farmsPot.data.models.Farmer;
import ng.farmsPot.data.models.Loan;
import ng.farmsPot.data.models.LoanStatus;
import ng.farmsPot.data.repositories.LoanRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;


import java.util.List;

@Service

public class CreditScoringService {

    private final LoanRepository loanRepository;
    private static final Logger logger = LoggerFactory.getLogger(CreditScoringService.class);

    public CreditScoringService(LoanRepository loanRepository) {
        this.loanRepository = loanRepository;

    }

    public int creditScore(Farmer farmer) {

        logger.info("CreditScoringService start");
        double yieldRatio = farmer.getHistoricAverageYieldKg() / 5000.0;
 logger.info(" i'm using (int) as a type cast since it will be in double so im trying to cast it back to int.");
        int yieldScore = (int) (yieldRatio * 40);
// in a situation the farmer's score is more that the given rate for land then it should round it down back to 40 for balance.
        if (yieldScore > 40) {
            yieldScore = 40;
        }


        double farmSizeRatio = farmer.getFarmSizeHectares() / 10.0;
        int farmSizeScore = (int) (farmSizeRatio * 20);

        if (farmSizeScore > 20) {
            farmSizeScore = 20;
        }
        List<Loan> pastLoans = loanRepository.findByFarmerIdOrderByIdAsc(farmer.getId());

        int repaymentScore = 20;

        if (!pastLoans.isEmpty()) {
            for (Loan pastLoan : pastLoans) {
                if (pastLoan.getStatus() == LoanStatus.FULLY_REPAID) {
                    repaymentScore = repaymentScore + 10;
                }
                if (pastLoan.getStatus() == LoanStatus.DEFAULTED) {
                    repaymentScore = repaymentScore - 25;
                }
            }
        }

        if (repaymentScore > 40) {
            repaymentScore = 40;
        }
        if (repaymentScore < 0) {
            repaymentScore = 0;
        }

        int result = yieldScore + repaymentScore + farmSizeScore;
        return result;
    }

}
