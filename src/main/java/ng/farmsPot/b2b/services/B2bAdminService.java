package ng.farmsPot.b2b.services;

import ng.farmsPot.b2b.dtos.requests.DecisionRequest;
import ng.farmsPot.b2b.dtos.responses.B2bStatsResponse;
import ng.farmsPot.b2b.dtos.responses.BuyerResponse;

import java.util.List;

public interface B2bAdminService {
    B2bStatsResponse stats();
    List<BuyerResponse> buyers(String status);
    BuyerResponse decideBuyer(int buyerId, DecisionRequest request);
}
