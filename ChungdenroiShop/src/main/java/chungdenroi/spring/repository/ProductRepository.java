package chungdenroi.spring.repository;

import chungdenroi.spring.model.Category;
import chungdenroi.spring.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import javax.persistence.criteria.Order;
import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findProductByNameIsLike(String name);
    List<Product> findAllByCategory(Category category);
//    Boolean
//    List<Product> findProductById(Order order);
    List<Product> findProductByCategory_Id(Long id);
    Integer countProductByCategory_Id(Long id);
    Integer countProductById(Long id);

    @Query("SELECT p FROM Product p")
    Page<Product> findProductsBy(Pageable pageable);

    List<Product> findTopBy();
    List<Product> findAllByPriceBetween(double min, double max);
    List<Product> findAllByPriceGreaterThan(double price);

//    @Query("SELECT Product .price FROM Product WHERE Product .id = ?1")
//    Double queryById(Long id);
}
