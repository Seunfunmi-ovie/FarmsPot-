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
import ng.farmsPot.data.models.Farmer;

import java.time.LocalDate;
import java.time.LocalDateTime;

/** Produce a farmer offers for export. Buyers only see it once an admin has approved it. */
@Data
@Entity
@Table(name = "export_listings")
public class ExportListing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "farmers_id", nullable = false)
    private Farmer farmer;

    private String crop;

    @Column(length = 1000)
    private String description;

    private String originLocation;
    private double availableQuantityKg;
    private double minOrderKg;
    private double pricePerKgUsd;
    private LocalDate availableFrom;

    @Enumerated(EnumType.STRING)
    private ListingStatus status = ListingStatus.PENDING_REVIEW;

    /** Admin's reason when a listing is rejected. */
    private String reviewNote;

    private LocalDateTime createdAt = LocalDateTime.now();
}
