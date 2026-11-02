package ng.farmsPot.b2b.services;

import ng.farmsPot.b2b.dtos.requests.PlaceOrderRequest;
import ng.farmsPot.b2b.dtos.responses.OrderResponse;

import java.util.List;

public interface ExportOrderService {
    OrderResponse place(int buyerId, PlaceOrderRequest request);
    List<OrderResponse> buyerOrders(int buyerId);
    OrderResponse cancel(int buyerId, int orderId);
    OrderResponse confirmDelivery(int buyerId, int orderId);
    List<OrderResponse> farmerOrders(int farmerId);
    OrderResponse accept(int farmerId, int orderId);
    OrderResponse reject(int farmerId, int orderId, String note);
    OrderResponse ship(int farmerId, int orderId);
    OrderResponse markPaid(int farmerId, int orderId);
    List<OrderResponse> adminList(String status);
}
