package ng.farmsPot.b2b.controllers;

import ng.farmsPot.b2b.dto.B2bStatsResponse;
import ng.farmsPot.b2b.dto.BuyerResponse;
import ng.farmsPot.b2b.dto.DecisionRequest;
import ng.farmsPot.b2b.dto.ListingResponse;
import ng.farmsPot.b2b.dto.OrderResponse;
import ng.farmsPot.b2b.service.B2bAdminService;
import ng.farmsPot.b2b.service.ExportOrderService;
import ng.farmsPot.b2b.service.ListingService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping("/api/admin/b2b")
public class B2bAdminController {

    private final B2bAdminService adminService;
    private final ListingService listingService;
    private final ExportOrderService orderService;

    public B2bAdminController(B2bAdminService adminService, ListingService listingService, ExportOrderService orderService) {
        this.adminService = adminService;
        this.listingService = listingService;
        this.orderService = orderService;
    }

    @GetMapping("/stats")
    public B2bStatsResponse stats() {
        return adminService.stats();
    }

    @GetMapping("/buyers")
    public List<BuyerResponse> buyers(@RequestParam(required = false) String status) {
        return adminService.buyers(status);
    }

    @PutMapping("/buyers/{buyerId}/decision")
    public BuyerResponse decideBuyer(@PathVariable int buyerId, @RequestBody DecisionRequest request) {
        return adminService.decideBuyer(buyerId, request);
    }

    @GetMapping("/listings")
    public List<ListingResponse> listings(@RequestParam(required = false) String status) {
        return listingService.adminList(status);
    }

    @PutMapping("/listings/{listingId}/decision")
    public ListingResponse decideListing(@PathVariable int listingId, @RequestBody DecisionRequest request) {
        return listingService.decide(listingId, request);
    }

    @GetMapping("/orders")
    public List<OrderResponse> orders(@RequestParam(required = false) String status) {
        return orderService.adminList(status);
    }
}
