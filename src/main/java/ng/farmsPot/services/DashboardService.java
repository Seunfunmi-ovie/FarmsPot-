package ng.farmsPot.services;

import ng.farmsPot.data.models.Farmer;
import ng.farmsPot.data.models.FarmersAccount;
import ng.farmsPot.data.models.Loan;
import ng.farmsPot.data.repositories.FarmersAccountRepository;
import ng.farmsPot.data.repositories.FarmersRepository;
import ng.farmsPot.data.repositories.LoanRepository;
import ng.farmsPot.dtos.responses.DashboardResponse;
import ng.farmsPot.dtos.responses.LoanSummaryResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DashboardService {

    private final FarmersRepository farmersRepository;
    private final FarmersAccountRepository farmersAccountRepository;
    private final LoanRepository loanRepository;

    public DashboardService(FarmersRepository farmersRepository,
                            FarmersAccountRepository farmersAccountRepository,
                            LoanRepository loanRepository) {
        this.farmersRepository = farmersRepository;
        this.farmersAccountRepository = farmersAccountRepository;
        this.loanRepository = loanRepository;
    }

    @Transactional(readOnly = true)
    public DashboardResponse getDashboard(int farmerId) {
        Farmer farmer = farmersRepository.findById(farmerId)
                .orElseThrow(() -> new IllegalArgumentException("No Farmer found with id " + farmerId));
        FarmersAccount account = farmersAccountRepository.findByFarmerId(farmerId)
                .orElseThrow(() -> new IllegalStateException("No account found for farmer " + farmerId));

        List<LoanSummaryResponse> loans = loanRepository.findByFarmerIdOrderByIdAsc(farmerId)
                .stream()
                .map(this::toSummary)
                .toList();

        DashboardResponse response = new DashboardResponse();
        response.setFullName(farmer.getFullName());
        response.setBalance(account.getBalance());
        response.setLoans(loans);
        return response;
    }

    private LoanSummaryResponse toSummary(Loan loan) {
        LoanSummaryResponse summary = new LoanSummaryResponse();
        summary.setLoanId(loan.getId());
        summary.setRequestedAmount(loan.getRequestedAmount());
        summary.setAmountRepaid(loan.getAmountRepaidViaEscrow());
        summary.setStatus(loan.getStatus());
        summary.setDisbursementDate(loan.getDisbursementDate());
        return summary;
    }
}