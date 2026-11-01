package ng.farmsPot.services;

import ng.farmsPot.dtos.requests.AdminLoanReviewRequest;
import ng.farmsPot.dtos.responses.AdminFarmerResponse;
import ng.farmsPot.dtos.responses.AdminLoanResponse;
import ng.farmsPot.dtos.responses.AdminStatsResponse;

import java.util.List;

public interface AdminService {
    AdminStatsResponse getStats();
    List<AdminLoanResponse> listLoans(String status);
    AdminLoanResponse reviewLoan(int loanId, AdminLoanReviewRequest request);
    List<AdminFarmerResponse> listFarmers();
    AdminFarmerResponse setSuspension(int adminId, int farmerId, boolean suspended);
}
