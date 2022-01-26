package chungdenroi.spring.repository;

import chungdenroi.spring.model.Category;
import chungdenroi.spring.model.Orders;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface OrdersRepository extends JpaRepository<Orders, Long> {
    List<Orders> findOrdersByNameIsLike(String name);

    @Query("SELECT o FROM Orders o")
    Page<Orders> findOrdersBy(Pageable pageable);

    Page<Orders> findAllByCustomer_Id(Pageable pageable, Long id);




    int countByOrderDateLike(String date);
    List<Orders> findOrdersByOrderDateLike(String date);

    List<Orders> findAllByCustomer_Id(Long id);

    int  countAllByStatus(String status);

    List<Orders> findAllByStatus(String status);


}
