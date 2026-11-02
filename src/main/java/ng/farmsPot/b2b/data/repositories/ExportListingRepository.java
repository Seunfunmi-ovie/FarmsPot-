package ng.farmsPot.b2b.data.repositories;

import ng.farmsPot.b2b.data.models.ExportListing;
import ng.farmsPot.b2b.data.models.ListingStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExportListingRepository extends JpaRepository<ExportListing, Integer> {
    List<ExportListing> findByStatus(ListingStatus status);
    List<ExportListing> findByFarmerId(int farmerId);
}
