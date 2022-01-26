package chungdenroi.spring.repository;

import chungdenroi.spring.model.Category;
import chungdenroi.spring.model.Customer;
import chungdenroi.spring.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
    List<Customer> findCustomerByFullnameLike(String name);

    boolean existsCustomerByMobilenumber(String mobilenumber);
    Customer findCustomerByMobilenumber(String mobilenumber);

    @Query("SELECT c FROM Customer c")
    Page<Customer> findCustomersBy(Pageable pageable);
}
