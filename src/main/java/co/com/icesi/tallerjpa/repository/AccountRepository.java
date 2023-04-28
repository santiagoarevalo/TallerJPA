package co.com.icesi.tallerjpa.repository;

import co.com.icesi.tallerjpa.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AccountRepository extends JpaRepository<Account, UUID> {

    @Query("SELECT CASE WHEN (COUNT(u) > 0) THEN true ELSE false END FROM Account u WHERE u.accountNumber = :accountNumber")
    boolean existsByAccountNumber(@Param("accountNumber") String accountNumber);
    @Query("SELECT a FROM Account a WHERE a.accountNumber = :accountNumber")
    Optional<Account> findByAccountNumber(@Param("accountNumber") String accountNumber);
    @Modifying
    @Query("UPDATE Account a SET a.balance = :balance WHERE a.accountNumber = :accountNumber")
    void updateBalance(@Param("balance") Long balance, @Param("accountNumber") String accountNumber);
    @Modifying
    @Query("UPDATE Account a SET a.active = CASE WHEN a.balance > 0 THEN true END WHERE (a.accountNumber = :accountNumber)")
    void enableAccount(@Param("accountNumber") String accountNumber);
    @Modifying
    @Query("UPDATE Account a SET a.active = CASE WHEN a.balance = 0 THEN false END WHERE (a.accountNumber = :accountNumber)")
    void disableAccount(@Param("accountNumber") String accountNumber);
    @Query("SELECT a.active FROM Account a WHERE a.accountNumber = :accountNumber")
    boolean isActive(@Param("accountNumber") String accountNumber);



}