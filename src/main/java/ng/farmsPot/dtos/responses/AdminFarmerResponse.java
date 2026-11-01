package ng.farmsPot.dtos.responses;

import lombok.Data;
import ng.farmsPot.data.models.Role;

import java.time.LocalDateTime;

@Data
public class AdminFarmerResponse {
    private int farmerId;
    private String fullName;
    private String userName;
    private String email;
    private String phoneNumber;
    private String farmLocation;
    private double farmSizeHectares;
    private double historicAverageYieldKg;
    private Role role;
    private boolean suspended;
    private LocalDateTime createdAt;
    private long loanCount;
    private double balance;
}
