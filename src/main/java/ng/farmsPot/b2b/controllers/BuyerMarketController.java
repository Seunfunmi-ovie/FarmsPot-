package ng.farmsPot.b2b.controllers;

import jakarta.validation.Valid;
import ng.farmsPot.b2b.dtos.responses.BuyerResponse;
import ng.farmsPot.b2b.dtos.responses.ListingResponse;
import ng.farmsPot.b2b.dtos.responses.OrderResponse;
import ng.farmsPot.b2b.dtos.requests.PlaceOrderRequest;
import ng.farmsPot.b2b.services.BuyerService;
import ng.farmsPot.b2b.services.ExportOrderService;
import ng.farmsPot.b2b.services.ListingService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** Everything a signed-in buyer does. Guarded by BuyerAuthInterceptor (see WebConfig). */
@RestController
@RequestMapping("/api/b2b/buyer")
public class BuyerMarketController {

    private final BuyerService buyerService;
    private final ListingService listingService;
    private final ExportOrderService orderService;

    public BuyerMarketController(BuyerService buyerService, ListingService listingService, ExportOrderService orderService) {
        this.buyerService = buyerService;
        this.listingService = listingService;
        this.orderService = orderService;
    }

    @GetMapping("/me")
    public BuyerResponse me(@RequestAttribute("authBuyerId") int buyerId) {
        return buyerService.profile(buyerId);
    }

    @GetMapping("/listings")
    public List<ListingResponse> listings(@RequestParam(required = false) String crop) {
        return listingService.browse(crop);
    }

    @GetMapping("/listings/{listingId}")
    public ListingResponse listing(@PathVariable int listingId) {
        return listingService.getActive(listingId);
    }

    @PostMapping("/orders")
    public OrderResponse placeOrder(@RequestAttribute("authBuyerId") int buyerId,
                                    @Valid @RequestBody PlaceOrderRequest request) {
        return orderService.place(buyerId, request);
    }

    @GetMapping("/orders")
    public List<OrderResponse> orders(@RequestAttribute("authBuyerId") int buyerId) {
        return orderService.buyerOrders(buyerId);
    }

    @PostMapping("/orders/{orderId}/cancel")
    public OrderResponse cancel(@RequestAttribute("authBuyerId") int buyerId, @PathVariable int orderId) {
        return orderService.cancel(buyerId, orderId);
    }

    @PostMapping("/orders/{orderId}/confirm-delivery")
    public OrderResponse confirmDelivery(@RequestAttribute("authBuyerId") int buyerId, @PathVariable int orderId) {
        return orderService.confirmDelivery(buyerId, orderId);
    }
}
