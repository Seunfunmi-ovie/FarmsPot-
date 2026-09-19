package ng.farmsPot.data.repositories;

import ng.farmsPot.data.models.Farmer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FarmersRepository extends JpaRepository<Farmer, Integer> {
  Optional <Farmer> findByPhoneNumber(String phoneNumber);
  Optional<Farmer> findByUserName(String userName);

}
