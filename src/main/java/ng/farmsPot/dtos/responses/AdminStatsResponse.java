package ng.farmsPot.dtos.responses;

import lombok.Data;

@Data
public class AdminStatsResponse {
    private long totalFarmers;
    private long totalLoans;
    private long pendingLoans;
    private long activeLoans;
    private long defaultedLoans;
    private long fullyRepaidLoans;
    private long rejectedLoans;
    private double totalDisbursed;
    private double totalRepaid;
    private double totalOutstanding;
    private double defaultRatePercent;
    private double totalAccountBalances;
}
