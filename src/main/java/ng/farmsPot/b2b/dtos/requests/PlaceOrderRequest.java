package ng.farmsPot.b2b.dtos.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class PlaceOrderRequest {
    @NotNull(message = "Choose a listing to order from")
    private Integer listingId;

    @Positive(message = "Quantity must be greater than zero")
    private double quantityKg;

    @NotBlank(message = "Destination country is required")
    private String destinationCountry;

    @NotBlank(message = "Delivery address is required")
    private String deliveryAddress;

    private String note;
}
