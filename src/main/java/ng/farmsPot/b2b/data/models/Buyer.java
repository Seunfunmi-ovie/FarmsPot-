package ng.farmsPot.b2b.data.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.LocalDateTime;

/** An international business that buys produce through the marketplace. */
@Data
@Entity
@Table(name = "buyers")
public class Buyer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String companyName;
    private String contactName;

    @Column(unique = true)
    private String email;

    private String phoneNumber;
    private String country;
    private String businessRegNumber;
    private String password;

    @Enumerated(EnumType.STRING)
    private BuyerStatus status = BuyerStatus.PENDING;

    /** Why the admin rejected (or a remark when verifying). */
    private String verificationNote;

    private LocalDateTime createdAt = LocalDateTime.now();
}
