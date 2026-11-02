package ng.farmsPot.b2b.dtos.responses;

import lombok.Data;
import ng.farmsPot.b2b.data.models.OrderStatus;

import java.time.LocalDateTime;

@Data
public class OrderResponse {
    private int orderId;
    private int listingId;
    private String crop;
    private int farmerId;
    private String farmerName;
    private int buyerId;
    private String buyerCompany;
    private String buyerCountry;
    private double quantityKg;
    private double unitPriceUsd;
    private double totalPriceUsd;
    private String destinationCountry;
    private String deliveryAddress;
    private String note;
    private OrderStatus status;
    private boolean paid;
    private String statusNote;
    private LocalDateTime createdAt;
}
