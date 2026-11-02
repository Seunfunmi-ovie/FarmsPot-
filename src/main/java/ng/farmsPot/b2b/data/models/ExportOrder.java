package ng.farmsPot.b2b.data.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.LocalDateTime;

/** A buyer's order against one listing. Payment is only recorded, not processed, in the app. */
@Data
@Entity
@Table(name = "export_orders")
public class ExportOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "listing_id", nullable = false)
    private ExportListing listing;

    @ManyToOne
    @JoinColumn(name = "buyer_id", nullable = false)
    private Buyer buyer;

    private double quantityKg;

    /** Price per kg at the moment the order was placed. */
    private double unitPriceUsd;

    private double totalPriceUsd;
    private String destinationCountry;

    @Column(length = 500)
    private String deliveryAddress;

    @Column(length = 1000)
    private String note;

    @Enumerated(EnumType.STRING)
    private OrderStatus status = OrderStatus.REQUESTED;

    private boolean paid;

    /** Reason given when the farmer rejects the order. */
    private String statusNote;

    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt = LocalDateTime.now();
}
