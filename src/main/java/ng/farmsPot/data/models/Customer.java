package ng.farmsPot.data.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Table(name = "customers")
@Data
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @NotBlank
    private String firstName;
    @NotBlank
    private String lastName;
    @Column(unique = true)
    private String email;
    @Column(unique = true)
    @NotBlank
    private String userName;
    @NotBlank
    private String password;
    @NotBlank
    @Column(unique = true)
    private String phoneNumber;

    @NotBlank
    private String address;

    private LocalDate createdDate;

}
