package ng.farmsPot.controllers;

import ng.farmsPot.dtos.requests.LoanApplicationRequest;
import ng.farmsPot.dtos.responses.LoanApplicationResponse;
import ng.farmsPot.services.LoanService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/loans")
public class LoanController {
    private final LoanService loanService;

    public LoanController(LoanService loanService) {
        this.loanService = loanService;
    }

    @PostMapping("/applyLoan")
    public LoanApplicationResponse requestLoan(@RequestBody LoanApplicationRequest loanApplicationRequest){
        return loanService.applyLoan(loanApplicationRequest);
    }

    @PostMapping("/disbursedLoan/{loanId}")
    public LoanApplicationResponse disbursedLoan(@PathVariable int loanId){
        return loanService.disburseLoan(loanId);

    }

    @PostMapping("/recordEscrowPayment/{loanId}/{amount}")
    public LoanApplicationResponse recordEscrowPayment(@PathVariable int loanId, @PathVariable double amount){
        return loanService.recordEscrowPayment(loanId, amount);
    }

}
