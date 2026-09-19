package ng.farmsPot.data.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "farmers")

public class Farmer {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private int id;
    @NotBlank
    private String fullName;
    private String email;
    @Column(unique = true)
    private String userName;
    @NotBlank
    private String password;
    @NotBlank
    @Column(unique = true)
    private String phoneNumber;
    private String farmLocation;
    private double farmSizeHectares;
    private double historicAverageYieldKg;
    private LocalDateTime createdAt =  LocalDateTime.now();
}
