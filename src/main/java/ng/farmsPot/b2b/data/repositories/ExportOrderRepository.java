package ng.farmsPot.b2b.data.repositories;

import ng.farmsPot.b2b.data.models.ExportOrder;
import ng.farmsPot.b2b.data.models.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExportOrderRepository extends JpaRepository<ExportOrder, Integer> {
    List<ExportOrder> findByBuyerId(int buyerId);
    List<ExportOrder> findByListingFarmerId(int farmerId);
    List<ExportOrder> findByStatus(OrderStatus status);
}
