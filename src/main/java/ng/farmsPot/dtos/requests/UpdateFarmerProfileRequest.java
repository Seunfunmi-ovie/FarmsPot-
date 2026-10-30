package ng.farmsPot.dtos.requests;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;


@Data
public class UpdateFarmerProfileRequest {

    @Size(min = 2, max = 100, message = "Full name must be between 2 and 100 characters")
    private String fullName;

    @Email(message = "Email address is not valid")
    private String email;

    @Pattern(regexp = "^[0-9+]{7,15}$", message = "Phone number must be 7 to 15 digits")
    private String phoneNumber;

    @Size(min = 2, max = 150, message = "Farm location must be between 2 and 150 characters")
    private String farmLocation;

    @Positive(message = "Farm size must be greater than zero")
    private Double farmSizeHectares;

    @Positive(message = "Historic average yield must be greater than zero")
    private Double historicAverageYieldKg;
}
