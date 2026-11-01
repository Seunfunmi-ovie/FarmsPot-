package ng.farmsPot.controllers;

import ng.farmsPot.dtos.requests.AdminLoanReviewRequest;
import ng.farmsPot.dtos.requests.AdminSuspensionRequest;
import ng.farmsPot.dtos.responses.AdminFarmerResponse;
import ng.farmsPot.dtos.responses.AdminLoanResponse;
import ng.farmsPot.dtos.responses.AdminStatsResponse;
import ng.farmsPot.services.AdminService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** Every route here is guarded by AdminAuthInterceptor (see WebConfig). */
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/stats")
    public AdminStatsResponse stats() {
        return adminService.getStats();
    }

    @GetMapping("/loans")
    public List<AdminLoanResponse> loans(@RequestParam(required = false) String status) {
        return adminService.listLoans(status);
    }

    @PutMapping("/loans/{loanId}/review")
    public AdminLoanResponse reviewLoan(@PathVariable int loanId, @RequestBody AdminLoanReviewRequest request) {
        return adminService.reviewLoan(loanId, request);
    }

    @GetMapping("/farmers")
    public List<AdminFarmerResponse> farmers() {
        return adminService.listFarmers();
    }

    @PutMapping("/farmers/{farmerId}/suspension")
    public AdminFarmerResponse setSuspension(@RequestAttribute("adminId") int adminId,
                                             @PathVariable int farmerId,
                                             @RequestBody AdminSuspensionRequest request) {
        return adminService.setSuspension(adminId, farmerId, request.isSuspended());
    }
}
