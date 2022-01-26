package chungdenroi.spring.repository;

import chungdenroi.spring.model.OrderProducts;
import chungdenroi.spring.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderProductsRepository extends JpaRepository<OrderProducts, Long> {
    List<OrderProducts> findAllByOrders_Id(Long id);
    void deleteAllByOrders_Id(Long id);

    int countAllByProduct(Product product);
}
