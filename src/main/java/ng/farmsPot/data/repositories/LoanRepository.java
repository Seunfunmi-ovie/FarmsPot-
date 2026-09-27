package ng.farmsPot.data.repositories;

import ng.farmsPot.data.models.Loan;
import ng.farmsPot.data.models.LoanStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface LoanRepository extends JpaRepository<Loan, Integer> {

    List<Loan> findByFarmerIdOrderByIdAsc(int farmerId);
    List<Loan> findByStatus(LoanStatus status);
}
