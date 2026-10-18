package ng.farmsPot.dtos.responses;

import lombok.Data;

import java.util.List;

@Data
public class DashboardResponse {
    private String fullName;
    private double balance;
    private List<LoanSummaryResponse> loans;
}