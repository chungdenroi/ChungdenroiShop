package chungdenroi.spring.repository;

import chungdenroi.spring.model.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findCategoryByNameIsLike(String name);
    Category getByName(String name);
    @Query("SELECT c FROM Category c")
    Page<Category> findCategoriesBy(Pageable pageable);
    boolean existsByUrl(String url);
    Category findByUrl(String url);
}
