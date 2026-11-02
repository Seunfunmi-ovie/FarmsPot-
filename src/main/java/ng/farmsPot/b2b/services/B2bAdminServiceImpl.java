package ng.farmsPot.b2b.services;

import ng.farmsPot.b2b.dtos.responses.B2bStatsResponse;
import ng.farmsPot.b2b.dtos.responses.BuyerResponse;
import ng.farmsPot.b2b.dtos.requests.DecisionRequest;
import ng.farmsPot.b2b.data.models.Buyer;
import ng.farmsPot.b2b.data.models.BuyerStatus;
import ng.farmsPot.b2b.data.models.ExportOrder;
import ng.farmsPot.b2b.data.models.ListingStatus;
import ng.farmsPot.b2b.data.models.OrderStatus;
import ng.farmsPot.b2b.data.repositories.BuyerRepository;
import ng.farmsPot.b2b.data.repositories.ExportListingRepository;
import ng.farmsPot.b2b.data.repositories.ExportOrderRepository;
import ng.farmsPot.b2b.utils.B2bMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
public class B2bAdminServiceImpl implements B2bAdminService {

    private final BuyerRepository buyerRepository;
    private final ExportListingRepository listingRepository;
    private final ExportOrderRepository orderRepository;

    public B2bAdminServiceImpl(BuyerRepository buyerRepository,
                           ExportListingRepository listingRepository,
                           ExportOrderRepository orderRepository) {
        this.buyerRepository = buyerRepository;
        this.listingRepository = listingRepository;
        this.orderRepository = orderRepository;
    }

    @Override
    public B2bStatsResponse stats() {
        List<ExportOrder> orders = orderRepository.findAll();

        B2bStatsResponse stats = new B2bStatsResponse();
        stats.setPendingBuyers(buyerRepository.findByStatus(BuyerStatus.PENDING).size());
        stats.setVerifiedBuyers(buyerRepository.findByStatus(BuyerStatus.VERIFIED).size());
        stats.setPendingListings(listingRepository.findByStatus(ListingStatus.PENDING_REVIEW).size());
        stats.setActiveListings(listingRepository.findByStatus(ListingStatus.ACTIVE).size());
        stats.setOpenOrders(orders.stream().filter(order ->
                order.getStatus() == OrderStatus.REQUESTED
                        || order.getStatus() == OrderStatus.ACCEPTED
                        || order.getStatus() == OrderStatus.SHIPPED).count());
        stats.setDeliveredOrders(orders.stream().filter(order -> order.getStatus() == OrderStatus.DELIVERED).count());
        stats.setOrderValueUsd(orders.stream()
                .filter(order -> order.getStatus() == OrderStatus.ACCEPTED
                        || order.getStatus() == OrderStatus.SHIPPED
                        || order.getStatus() == OrderStatus.DELIVERED)
                .mapToDouble(ExportOrder::getTotalPriceUsd)
                .sum());
        return stats;
    }

    @Override
    public List<BuyerResponse> buyers(String status) {
        List<Buyer> buyers;
        if (status == null || status.isBlank() || status.equalsIgnoreCase("ALL")) {
            buyers = buyerRepository.findAll();
        } else {
            BuyerStatus parsed;
            try {
                parsed = BuyerStatus.valueOf(status.trim().toUpperCase());
            } catch (IllegalArgumentException exception) {
                throw new IllegalArgumentException("Unknown buyer status: " + status);
            }
            buyers = buyerRepository.findByStatus(parsed);
        }
        return buyers.stream()
                .sorted(Comparator.comparingInt(Buyer::getId).reversed())
                .map(B2bMapper::toBuyer)
                .toList();
    }

    @Transactional
    @Override
    public BuyerResponse decideBuyer(int buyerId, DecisionRequest request) {
        String decision = request.getDecision() == null ? "" : request.getDecision().trim().toUpperCase();
        String note = request.getNote() == null ? "" : request.getNote().trim();
        Buyer buyer = buyerRepository.findById(buyerId)
                .orElseThrow(() -> new IllegalArgumentException("No Buyer found with id " + buyerId));

        switch (decision) {
            case "VERIFY" -> {
                if (buyer.getStatus() == BuyerStatus.VERIFIED) {
                    throw new IllegalStateException("This company is already verified.");
                }
                buyer.setStatus(BuyerStatus.VERIFIED);
                buyer.setVerificationNote(note.isEmpty() ? null : note);
            }
            case "REJECT" -> {
                if (note.isEmpty()) {
                    throw new IllegalArgumentException("Please give a reason for rejecting this company.");
                }
                if (buyer.getStatus() == BuyerStatus.REJECTED) {
                    throw new IllegalStateException("This company is already rejected.");
                }
                buyer.setStatus(BuyerStatus.REJECTED);
                buyer.setVerificationNote(note);
            }
            default -> throw new IllegalArgumentException("Decision must be VERIFY or REJECT.");
        }
        return B2bMapper.toBuyer(buyerRepository.save(buyer));
    }
}
