package co.com.icesi.jpataller.repository;

import co.com.icesi.jpataller.model.IcesiAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface IcesiAccountRepository extends JpaRepository<IcesiAccount, UUID> {

    Optional<IcesiAccount> findByAccountNumber(String accountNumber);

}
