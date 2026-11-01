package ng.farmsPot.config;

import ng.farmsPot.data.models.Loan;
import ng.farmsPot.data.repositories.LoanRepository;
import ng.farmsPot.exceptions.ForbiddenException;
import org.springframework.stereotype.Component;

/** Ownership checks used by controllers: a farmer may only act on their own data. */
@Component
public class AccessGuard {

    private final LoanRepository loanRepository;

    public AccessGuard(LoanRepository loanRepository) {
        this.loanRepository = loanRepository;
    }

    public void requireSelf(int authFarmerId, int farmerId) {
        if (authFarmerId != farmerId) {
            throw new ForbiddenException("You can only access your own account.");
        }
    }

    public void requireLoanOwner(int authFarmerId, int loanId) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new IllegalArgumentException("No Loan found with id " + loanId));
        if (loan.getFarmer().getId() != authFarmerId) {
            throw new ForbiddenException("You can only act on your own loans.");
        }
    }
}
