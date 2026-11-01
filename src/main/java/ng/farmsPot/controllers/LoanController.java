package ng.farmsPot.controllers;

import ng.farmsPot.config.AccessGuard;
import ng.farmsPot.dtos.requests.LoanApplicationRequest;
import ng.farmsPot.dtos.responses.LoanApplicationResponse;
import ng.farmsPot.services.LoanService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/loans")
public class LoanController {
    private final LoanService loanService;
    private final AccessGuard accessGuard;

    public LoanController(LoanService loanService, AccessGuard accessGuard) {
        this.loanService = loanService;
        this.accessGuard = accessGuard;
    }

    @PostMapping("/applyLoan")
    public LoanApplicationResponse requestLoan(@RequestAttribute("authFarmerId") int authFarmerId,
                                               @RequestBody LoanApplicationRequest loanApplicationRequest){
        accessGuard.requireSelf(authFarmerId, loanApplicationRequest.getFarmerId());
        return loanService.applyLoan(loanApplicationRequest);
    }

    @PostMapping("/disbursedLoan/{loanId}")
    public LoanApplicationResponse disbursedLoan(@PathVariable int loanId){
        return loanService.disburseLoan(loanId);

    }

    @PostMapping("/recordEscrowPayment/{loanId}/{amount}")
    public LoanApplicationResponse recordEscrowPayment(@RequestAttribute("authFarmerId") int authFarmerId,
                                                       @PathVariable int loanId, @PathVariable double amount){
        accessGuard.requireLoanOwner(authFarmerId, loanId);
        return loanService.recordEscrowPayment(loanId, amount);
    }

}