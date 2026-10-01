package ng.farmsPot.data.repositories;

import ng.farmsPot.data.models.FarmersAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FarmersAccountRepository extends JpaRepository<FarmersAccount, Integer> {

    Optional<FarmersAccount> findByFarmerId(int farmerId);
}
