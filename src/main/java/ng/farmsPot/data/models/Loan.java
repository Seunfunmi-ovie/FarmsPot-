package ng.farmsPot.data.models;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "loans")
@Data
public class Loan {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private int id;

    @ManyToOne
    @JoinColumn(name = "farmers_id", nullable = false)
    private Farmer farmer;

    private double requestedAmount;

    @Enumerated(EnumType.STRING)
    private LoanStatus status;

    private String disbursementReference;

    private double amountRepaidViaEscrow;

    private Integer creditScore;

    private PaymentMethod paymentMethod;

    private boolean escrowEnabled;
}