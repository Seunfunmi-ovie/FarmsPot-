package ng.farmsPot.b2b.data.repositories;

import ng.farmsPot.b2b.data.models.Buyer;
import ng.farmsPot.b2b.data.models.BuyerStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BuyerRepository extends JpaRepository<Buyer, Integer> {
    Optional<Buyer> findByEmail(String email);
    List<Buyer> findByStatus(BuyerStatus status);
}
