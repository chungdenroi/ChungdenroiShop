package chungdenroi.spring.repository;

import chungdenroi.spring.model.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderDetailRepository extends JpaRepository<OrderDetail, Long> {
    OrderDetail findOrderDetailByOrder_Id(Long id);
}
