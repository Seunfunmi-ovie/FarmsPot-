package ng.farmsPot.b2b.dtos.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CreateListingRequest {
    @NotBlank(message = "Crop is required")
    private String crop;

    private String description;

    @NotBlank(message = "Where the produce is grown is required")
    private String originLocation;

    @Positive(message = "Available quantity must be greater than zero")
    private double availableQuantityKg;

    @Positive(message = "Minimum order must be greater than zero")
    private double minOrderKg;

    @Positive(message = "Price per kg must be greater than zero")
    private double pricePerKgUsd;

    @NotNull(message = "Say when the produce will be available")
    private LocalDate availableFrom;
}
