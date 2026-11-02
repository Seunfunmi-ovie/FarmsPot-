package ng.farmsPot.b2b.services;

import ng.farmsPot.b2b.dtos.requests.CreateListingRequest;
import ng.farmsPot.b2b.dtos.requests.DecisionRequest;
import ng.farmsPot.b2b.dtos.responses.ListingResponse;

import java.util.List;

public interface ListingService {
    ListingResponse create(int farmerId, CreateListingRequest request);
    List<ListingResponse> forFarmer(int farmerId);
    ListingResponse close(int farmerId, int listingId);
    List<ListingResponse> browse(String crop);
    ListingResponse getActive(int listingId);
    List<ListingResponse> adminList(String status);
    ListingResponse decide(int listingId, DecisionRequest request);
}
