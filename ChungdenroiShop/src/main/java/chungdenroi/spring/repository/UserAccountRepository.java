package chungdenroi.spring.repository;

import chungdenroi.spring.model.Category;
import chungdenroi.spring.model.UserAccount;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserAccountRepository extends JpaRepository<UserAccount, Long> {
//    Boolean findUserAccountByUsernameAndPassword(String username, String password);
    UserAccount findUserAccountByUsernameAndPassword(String username, String password);

    Optional<UserAccount> findByUsernameAndPassword(String username, String password);

    boolean existsUserAccountByUsernameAndPasswordAndPermission(String username, String pass, String per);
    boolean existsUserAccountByUsernameOrMobilenumberOrEmail(String username, String mobile, String email);
//    Optional<UserAccount> findByPe
    boolean existsByUsername(String username);

    List<UserAccount> findUserAccountByUsernameLike(String username);

    @Query("SELECT u FROM UserAccount u")
    Page<UserAccount> findUserAccountsBy(Pageable pageable);

    UserAccount findUserAccountByUsername(String username);

    UserAccount findUserAccountByMobilenumber(String mobile);

}
