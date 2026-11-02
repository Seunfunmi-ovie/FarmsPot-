package ng.farmsPot.b2b.dtos.responses;

import lombok.Data;
import ng.farmsPot.b2b.data.models.ListingStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class ListingResponse {
    private int listingId;
    private int farmerId;
    private String farmerName;
    private String crop;
    private String description;
    private String originLocation;
    private double availableQuantityKg;
    private double minOrderKg;
    private double pricePerKgUsd;
    private LocalDate availableFrom;
    private ListingStatus status;
    private String reviewNote;
    private LocalDateTime createdAt;
}
