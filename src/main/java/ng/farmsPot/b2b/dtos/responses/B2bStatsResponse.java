package ng.farmsPot.b2b.dtos.responses;

import lombok.Data;

@Data
public class B2bStatsResponse {
    private long pendingBuyers;
    private long verifiedBuyers;
    private long pendingListings;
    private long activeListings;
    private long openOrders;
    private long deliveredOrders;
    private double orderValueUsd;
}
