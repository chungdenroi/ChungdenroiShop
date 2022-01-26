package chungdenroi.spring.repository;

import chungdenroi.spring.model.Product;
import chungdenroi.spring.model.ProductDetail;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;


public interface ProductDetailRepository extends JpaRepository<ProductDetail, Long> {
    ProductDetail findProductDetailByProduct_Id(Long id);
    ProductDetail findProductDetailByUrl(String url);
    Optional<ProductDetail> findByUrl(String url);
    List<ProductDetail> findAllByQuantityIsGreaterThan(int quantity);

//    List<ProductDetail> findDistinctBySize(List<String> sizes);

    List<ProductDetail> findAllBySizeAndQuantityGreaterThan(String size, int quantity);
    boolean existsByUrl(String url);

    @Query("SELECT pd FROM ProductDetail pd")
    Page<ProductDetail> findProductDetailsByQuantityGreaterThan(Pageable page, int quantity);


}
