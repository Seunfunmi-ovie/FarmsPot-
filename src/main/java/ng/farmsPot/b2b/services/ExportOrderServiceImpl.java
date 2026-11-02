package ng.farmsPot.b2b.services;

import ng.farmsPot.b2b.dtos.responses.OrderResponse;
import ng.farmsPot.b2b.dtos.requests.PlaceOrderRequest;
import ng.farmsPot.b2b.data.models.Buyer;
import ng.farmsPot.b2b.data.models.BuyerStatus;
import ng.farmsPot.b2b.data.models.ExportListing;
import ng.farmsPot.b2b.data.models.ExportOrder;
import ng.farmsPot.b2b.data.models.ListingStatus;
import ng.farmsPot.b2b.data.models.OrderStatus;
import ng.farmsPot.b2b.data.repositories.BuyerRepository;
import ng.farmsPot.b2b.data.repositories.ExportListingRepository;
import ng.farmsPot.b2b.data.repositories.ExportOrderRepository;
import ng.farmsPot.b2b.utils.B2bMapper;
import ng.farmsPot.exceptions.ForbiddenException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
public class ExportOrderServiceImpl implements ExportOrderService {

    private final ExportOrderRepository orderRepository;
    private final ExportListingRepository listingRepository;
    private final BuyerRepository buyerRepository;

    public ExportOrderServiceImpl(ExportOrderRepository orderRepository,
                              ExportListingRepository listingRepository,
                              BuyerRepository buyerRepository) {
        this.orderRepository = orderRepository;
        this.listingRepository = listingRepository;
        this.buyerRepository = buyerRepository;
    }

    // ---- buyer side ----

    @Transactional
    @Override
    public OrderResponse place(int buyerId, PlaceOrderRequest request) {
        Buyer buyer = buyerRepository.findById(buyerId)
                .orElseThrow(() -> new IllegalArgumentException("No Buyer found with id " + buyerId));
        if (buyer.getStatus() != BuyerStatus.VERIFIED) {
            throw new IllegalStateException("Your company has not been verified yet, so you can't place orders.");
        }

        ExportListing listing = listingRepository.findById(request.getListingId())
                .orElseThrow(() -> new IllegalArgumentException("This listing is no longer available"));
        if (listing.getStatus() != ListingStatus.ACTIVE) {
            throw new IllegalArgumentException("This listing is no longer available");
        }

        double quantity = request.getQuantityKg();
        if (quantity < listing.getMinOrderKg()) {
            throw new IllegalArgumentException("The minimum order for this listing is " + listing.getMinOrderKg() + " kg");
        }
        if (quantity > listing.getAvailableQuantityKg()) {
            throw new IllegalArgumentException("Only " + listing.getAvailableQuantityKg() + " kg are available");
        }

        ExportOrder order = new ExportOrder();
        order.setListing(listing);
        order.setBuyer(buyer);
        order.setQuantityKg(quantity);
        order.setUnitPriceUsd(listing.getPricePerKgUsd());
        order.setTotalPriceUsd(Math.round(quantity * listing.getPricePerKgUsd() * 100) / 100.0);
        order.setDestinationCountry(request.getDestinationCountry().trim());
        order.setDeliveryAddress(request.getDeliveryAddress().trim());
        order.setNote(request.getNote() == null || request.getNote().isBlank() ? null : request.getNote().trim());
        order.setStatus(OrderStatus.REQUESTED);

        return B2bMapper.toOrder(orderRepository.save(order));
    }

    @Override
    public List<OrderResponse> buyerOrders(int buyerId) {
        return orderRepository.findByBuyerId(buyerId).stream()
                .sorted(Comparator.comparingInt(ExportOrder::getId).reversed())
                .map(B2bMapper::toOrder)
                .toList();
    }

    @Transactional
    @Override
    public OrderResponse cancel(int buyerId, int orderId) {
        ExportOrder order = ownedByBuyer(buyerId, orderId);
        if (order.getStatus() != OrderStatus.REQUESTED) {
            throw new IllegalStateException("Only orders that haven't been accepted yet can be cancelled.");
        }
        return move(order, OrderStatus.CANCELLED);
    }

    @Transactional
    @Override
    public OrderResponse confirmDelivery(int buyerId, int orderId) {
        ExportOrder order = ownedByBuyer(buyerId, orderId);
        if (order.getStatus() != OrderStatus.SHIPPED) {
            throw new IllegalStateException("Only shipped orders can be marked as delivered.");
        }
        return move(order, OrderStatus.DELIVERED);
    }

    // ---- farmer side ----

    @Override
    public List<OrderResponse> farmerOrders(int farmerId) {
        return orderRepository.findByListingFarmerId(farmerId).stream()
                .sorted(Comparator.comparingInt(ExportOrder::getId).reversed())
                .map(B2bMapper::toOrder)
                .toList();
    }

    @Transactional
    @Override
    public OrderResponse accept(int farmerId, int orderId) {
        ExportOrder order = ownedByFarmer(farmerId, orderId);
        if (order.getStatus() != OrderStatus.REQUESTED) {
            throw new IllegalStateException("Only new orders can be accepted.");
        }

        ExportListing listing = order.getListing();
        if (listing.getStatus() != ListingStatus.ACTIVE) {
            throw new IllegalStateException("This listing is no longer active.");
        }
        if (order.getQuantityKg() > listing.getAvailableQuantityKg()) {
            throw new IllegalStateException("You only have " + listing.getAvailableQuantityKg()
                    + " kg left on this listing, which is less than this order.");
        }

        listing.setAvailableQuantityKg(listing.getAvailableQuantityKg() - order.getQuantityKg());
        if (listing.getAvailableQuantityKg() <= 0) {
            listing.setStatus(ListingStatus.CLOSED);
        }
        listingRepository.save(listing);

        return move(order, OrderStatus.ACCEPTED);
    }

    @Transactional
    @Override
    public OrderResponse reject(int farmerId, int orderId, String note) {
        ExportOrder order = ownedByFarmer(farmerId, orderId);
        if (order.getStatus() != OrderStatus.REQUESTED) {
            throw new IllegalStateException("Only new orders can be rejected.");
        }
        order.setStatusNote(note == null || note.isBlank() ? null : note.trim());
        return move(order, OrderStatus.REJECTED);
    }

    @Transactional
    @Override
    public OrderResponse ship(int farmerId, int orderId) {
        ExportOrder order = ownedByFarmer(farmerId, orderId);
        if (order.getStatus() != OrderStatus.ACCEPTED) {
            throw new IllegalStateException("Only accepted orders can be marked as shipped.");
        }
        return move(order, OrderStatus.SHIPPED);
    }

    @Transactional
    @Override
    public OrderResponse markPaid(int farmerId, int orderId) {
        ExportOrder order = ownedByFarmer(farmerId, orderId);
        boolean payable = order.getStatus() == OrderStatus.ACCEPTED
                || order.getStatus() == OrderStatus.SHIPPED
                || order.getStatus() == OrderStatus.DELIVERED;
        if (!payable) {
            throw new IllegalStateException("Accept the order before recording a payment.");
        }
        if (order.isPaid()) {
            throw new IllegalStateException("This order is already marked as paid.");
        }
        order.setPaid(true);
        order.setUpdatedAt(LocalDateTime.now());
        return B2bMapper.toOrder(orderRepository.save(order));
    }

    // ---- admin side ----

    @Override
    public List<OrderResponse> adminList(String status) {
        List<ExportOrder> orders;
        if (status == null || status.isBlank() || status.equalsIgnoreCase("ALL")) {
            orders = orderRepository.findAll();
        } else {
            OrderStatus parsed;
            try {
                parsed = OrderStatus.valueOf(status.trim().toUpperCase());
            } catch (IllegalArgumentException exception) {
                throw new IllegalArgumentException("Unknown order status: " + status);
            }
            orders = orderRepository.findByStatus(parsed);
        }
        return orders.stream()
                .sorted(Comparator.comparingInt(ExportOrder::getId).reversed())
                .map(B2bMapper::toOrder)
                .toList();
    }

    // ---- helpers ----

    private ExportOrder find(int orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("No order found with id " + orderId));
    }

    private ExportOrder ownedByBuyer(int buyerId, int orderId) {
        ExportOrder order = find(orderId);
        if (order.getBuyer().getId() != buyerId) {
            throw new ForbiddenException("You can only act on your own orders.");
        }
        return order;
    }

    private ExportOrder ownedByFarmer(int farmerId, int orderId) {
        ExportOrder order = find(orderId);
        if (order.getListing().getFarmer().getId() != farmerId) {
            throw new ForbiddenException("You can only act on orders for your own listings.");
        }
        return order;
    }

    private OrderResponse move(ExportOrder order, OrderStatus next) {
        order.setStatus(next);
        order.setUpdatedAt(LocalDateTime.now());
        return B2bMapper.toOrder(orderRepository.save(order));
    }
}
