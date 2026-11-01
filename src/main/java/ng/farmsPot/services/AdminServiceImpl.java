package ng.farmsPot.services;

import ng.farmsPot.data.models.Farmer;
import ng.farmsPot.data.models.FarmersAccount;
import ng.farmsPot.data.models.Loan;
import ng.farmsPot.data.models.LoanStatus;
import ng.farmsPot.data.models.Role;
import ng.farmsPot.data.repositories.FarmersAccountRepository;
import ng.farmsPot.data.repositories.FarmersRepository;
import ng.farmsPot.data.repositories.LoanRepository;
import ng.farmsPot.dtos.requests.AdminLoanReviewRequest;
import ng.farmsPot.dtos.responses.AdminFarmerResponse;
import ng.farmsPot.dtos.responses.AdminLoanResponse;
import ng.farmsPot.dtos.responses.AdminStatsResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AdminServiceImpl implements AdminService {

    private final LoanRepository loanRepository;
    private final FarmersRepository farmersRepository;
    private final FarmersAccountRepository farmersAccountRepository;

    public AdminServiceImpl(LoanRepository loanRepository, FarmersRepository farmersRepository, FarmersAccountRepository farmersAccountRepository) {
        this.loanRepository = loanRepository;
        this.farmersRepository = farmersRepository;
        this.farmersAccountRepository = farmersAccountRepository;
    }

    @Override
    public AdminStatsResponse getStats() {
        List<Loan> loans = loanRepository.findAll();
        List<Farmer> farmers = farmersRepository.findAll();

        List<Loan> disbursed = loans.stream().filter(loan -> loan.getDisbursementDate() != null).toList();
        long defaulted = countByStatus(loans, LoanStatus.DEFAULTED);

        AdminStatsResponse stats = new AdminStatsResponse();
        stats.setTotalFarmers(farmers.stream().filter(farmer -> farmer.getRole() != Role.ADMIN).count());
        stats.setTotalLoans(loans.size());
        stats.setPendingLoans(countByStatus(loans, LoanStatus.PENDING));
        stats.setActiveLoans(countByStatus(loans, LoanStatus.APPROVED) + countByStatus(loans, LoanStatus.REPAYING));
        stats.setDefaultedLoans(defaulted);
        stats.setFullyRepaidLoans(countByStatus(loans, LoanStatus.FULLY_REPAID));
        stats.setRejectedLoans(countByStatus(loans, LoanStatus.REJECTED));

        stats.setTotalDisbursed(disbursed.stream().mapToDouble(Loan::getRequestedAmount).sum());
        stats.setTotalRepaid(loans.stream().mapToDouble(Loan::getAmountRepaidViaEscrow).sum());
        stats.setTotalOutstanding(loans.stream()
                .filter(loan -> loan.getStatus() == LoanStatus.APPROVED
                        || loan.getStatus() == LoanStatus.REPAYING
                        || loan.getStatus() == LoanStatus.DEFAULTED)
                .mapToDouble(loan -> Math.max(0, loan.getRequestedAmount() - loan.getAmountRepaidViaEscrow()))
                .sum());

        double rate = disbursed.isEmpty() ? 0 : (defaulted * 100.0) / disbursed.size();
        stats.setDefaultRatePercent(Math.round(rate * 10) / 10.0);

        stats.setTotalAccountBalances(farmersAccountRepository.findAll().stream()
                .mapToDouble(FarmersAccount::getBalance)
                .sum());
        return stats;
    }

    @Override
    public List<AdminLoanResponse> listLoans(String status) {
        List<Loan> loans;
        if (status == null || status.isBlank() || status.equalsIgnoreCase("ALL")) {
            loans = loanRepository.findAll();
        } else {
            LoanStatus parsed;
            try {
                parsed = LoanStatus.valueOf(status.trim().toUpperCase());
            } catch (IllegalArgumentException exception) {
                throw new IllegalArgumentException("Unknown loan status: " + status);
            }
            loans = loanRepository.findByStatus(parsed);
        }
        return loans.stream()
                .sorted(Comparator.comparingInt(Loan::getId).reversed())
                .map(this::toLoanResponse)
                .toList();
    }

    @Transactional
    @Override
    public AdminLoanResponse reviewLoan(int loanId, AdminLoanReviewRequest request) {
        String decision = request.getDecision() == null ? "" : request.getDecision().trim().toUpperCase();
        String note = request.getNote() == null ? "" : request.getNote().trim();
        if (note.isEmpty()) {
            throw new IllegalArgumentException("Please give a reason for this decision.");
        }

        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new IllegalArgumentException("No Loan found with id " + loanId));

        switch (decision) {
            case "REJECT" -> {
                if (loan.getStatus() != LoanStatus.PENDING) {
                    throw new IllegalStateException("Only loans awaiting release can be rejected.");
                }
                loan.setStatus(LoanStatus.REJECTED);
            }
            case "APPROVE" -> {
                if (loan.getStatus() != LoanStatus.REJECTED) {
                    throw new IllegalStateException("Only rejected loans can be overridden and approved.");
                }
                // Back to PENDING: it can now be released like any accepted loan.
                loan.setStatus(LoanStatus.PENDING);
            }
            default -> throw new IllegalArgumentException("Decision must be APPROVE or REJECT.");
        }

        loan.setReviewNote(note);
        return toLoanResponse(loanRepository.save(loan));
    }

    @Override
    public List<AdminFarmerResponse> listFarmers() {
        Map<Integer, Long> loanCounts = loanRepository.findAll().stream()
                .collect(Collectors.groupingBy(loan -> loan.getFarmer().getId(), Collectors.counting()));
        Map<Integer, Double> balances = farmersAccountRepository.findAll().stream()
                .collect(Collectors.toMap(account -> account.getFarmer().getId(), FarmersAccount::getBalance, (first, second) -> first));

        return farmersRepository.findAll().stream()
                .sorted(Comparator.comparingInt(Farmer::getId).reversed())
                .map(farmer -> toFarmerResponse(farmer,
                        loanCounts.getOrDefault(farmer.getId(), 0L),
                        balances.getOrDefault(farmer.getId(), 0.0)))
                .toList();
    }

    @Transactional
    @Override
    public AdminFarmerResponse setSuspension(int adminId, int farmerId, boolean suspended) {
        Farmer farmer = farmersRepository.findById(farmerId)
                .orElseThrow(() -> new IllegalArgumentException("No Farmer found with id " + farmerId));
        if (farmer.getId() == adminId) {
            throw new IllegalArgumentException("You can't suspend your own account.");
        }
        if (farmer.getRole() == Role.ADMIN) {
            throw new IllegalArgumentException("Admin accounts can't be suspended here.");
        }

        farmer.setSuspended(suspended);
        Farmer saved = farmersRepository.save(farmer);

        long loanCount = loanRepository.findByFarmerIdOrderByIdAsc(saved.getId()).size();
        double balance = farmersAccountRepository.findByFarmerId(saved.getId())
                .map(FarmersAccount::getBalance)
                .orElse(0.0);
        return toFarmerResponse(saved, loanCount, balance);
    }

    private long countByStatus(List<Loan> loans, LoanStatus status) {
        return loans.stream().filter(loan -> loan.getStatus() == status).count();
    }

    private AdminLoanResponse toLoanResponse(Loan loan) {
        AdminLoanResponse response = new AdminLoanResponse();
        response.setLoanId(loan.getId());
        response.setFarmerId(loan.getFarmer().getId());
        response.setFarmerName(loan.getFarmer().getFullName());
        response.setRequestedAmount(loan.getRequestedAmount());
        response.setCreditScore(loan.getCreditScore());
        response.setStatus(loan.getStatus());
        response.setAmountRepaid(loan.getAmountRepaidViaEscrow());
        response.setDisbursementDate(loan.getDisbursementDate());
        response.setPaymentMethod(loan.getPaymentMethod());
        response.setReviewNote(loan.getReviewNote());
        return response;
    }

    private AdminFarmerResponse toFarmerResponse(Farmer farmer, long loanCount, double balance) {
        AdminFarmerResponse response = new AdminFarmerResponse();
        response.setFarmerId(farmer.getId());
        response.setFullName(farmer.getFullName());
        response.setUserName(farmer.getUserName());
        response.setEmail(farmer.getEmail());
        response.setPhoneNumber(farmer.getPhoneNumber());
        response.setFarmLocation(farmer.getFarmLocation());
        response.setFarmSizeHectares(farmer.getFarmSizeHectares());
        response.setHistoricAverageYieldKg(farmer.getHistoricAverageYieldKg());
        response.setRole(farmer.getRole() == null ? Role.FARMER : farmer.getRole());
        response.setSuspended(Boolean.TRUE.equals(farmer.getSuspended()));
        response.setCreatedAt(farmer.getCreatedAt());
        response.setLoanCount(loanCount);
        response.setBalance(balance);
        return response;
    }
}
