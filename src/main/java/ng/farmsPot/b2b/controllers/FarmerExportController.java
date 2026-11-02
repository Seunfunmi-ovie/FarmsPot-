package ng.farmsPot.b2b.controllers;

import jakarta.validation.Valid;
import ng.farmsPot.b2b.dto.CreateListingRequest;
import ng.farmsPot.b2b.dto.ListingResponse;
import ng.farmsPot.b2b.dto.NoteRequest;
import ng.farmsPot.b2b.dto.OrderResponse;
import ng.farmsPot.b2b.service.ExportOrderService;
import ng.farmsPot.b2b.service.ListingService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** What a signed-in farmer does on the export marketplace. Guarded by FarmerAuthInterceptor. */
@RestController
@RequestMapping("/api/b2b/farmer")
public class FarmerExportController {

    private final ListingService listingService;
    private final ExportOrderService orderService;

    public FarmerExportController(ListingService listingService, ExportOrderService orderService) {
        this.listingService = listingService;
        this.orderService = orderService;
    }

    @PostMapping("/listings")
    public ListingResponse createListing(@RequestAttribute("authFarmerId") int farmerId,
                                         @Valid @RequestBody CreateListingRequest request) {
        return listingService.create(farmerId, request);
    }

    @GetMapping("/listings")
    public List<ListingResponse> listings(@RequestAttribute("authFarmerId") int farmerId) {
        return listingService.forFarmer(farmerId);
    }

    @PostMapping("/listings/{listingId}/close")
    public ListingResponse closeListing(@RequestAttribute("authFarmerId") int farmerId, @PathVariable int listingId) {
        return listingService.close(farmerId, listingId);
    }

    @GetMapping("/orders")
    public List<OrderResponse> orders(@RequestAttribute("authFarmerId") int farmerId) {
        return orderService.farmerOrders(farmerId);
    }

    @PostMapping("/orders/{orderId}/accept")
    public OrderResponse accept(@RequestAttribute("authFarmerId") int farmerId, @PathVariable int orderId) {
        return orderService.accept(farmerId, orderId);
    }

    @PostMapping("/orders/{orderId}/reject")
    public OrderResponse reject(@RequestAttribute("authFarmerId") int farmerId, @PathVariable int orderId,
                                @RequestBody(required = false) NoteRequest request) {
        return orderService.reject(farmerId, orderId, request == null ? null : request.getNote());
    }

    @PostMapping("/orders/{orderId}/ship")
    public OrderResponse ship(@RequestAttribute("authFarmerId") int farmerId, @PathVariable int orderId) {
        return orderService.ship(farmerId, orderId);
    }

    @PostMapping("/orders/{orderId}/mark-paid")
    public OrderResponse markPaid(@RequestAttribute("authFarmerId") int farmerId, @PathVariable int orderId) {
        return orderService.markPaid(farmerId, orderId);
    }
}
