package ng.farmsPot.b2b.services;

import ng.farmsPot.b2b.dtos.requests.BuyerLoginRequest;
import ng.farmsPot.b2b.dtos.requests.BuyerRegisterRequest;
import ng.farmsPot.b2b.dtos.responses.BuyerLoginResponse;
import ng.farmsPot.b2b.dtos.responses.BuyerResponse;

public interface BuyerService {
    BuyerResponse register(BuyerRegisterRequest request);
    BuyerLoginResponse login(BuyerLoginRequest request);
    BuyerResponse profile(int buyerId);
}
