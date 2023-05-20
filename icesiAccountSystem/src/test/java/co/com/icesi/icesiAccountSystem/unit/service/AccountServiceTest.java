package co.com.icesi.icesiAccountSystem.unit.service;

import co.com.icesi.icesiAccountSystem.dto.RequestAccountDTO;
import co.com.icesi.icesiAccountSystem.dto.ResponseAccountDTO;
import co.com.icesi.icesiAccountSystem.dto.TransactionOperationDTO;
import co.com.icesi.icesiAccountSystem.enums.AccountType;
import co.com.icesi.icesiAccountSystem.mapper.AccountMapper;
import co.com.icesi.icesiAccountSystem.mapper.AccountMapperImpl;
import co.com.icesi.icesiAccountSystem.model.IcesiAccount;
import co.com.icesi.icesiAccountSystem.model.IcesiRole;
import co.com.icesi.icesiAccountSystem.model.IcesiUser;
import co.com.icesi.icesiAccountSystem.repository.AccountRepository;
import co.com.icesi.icesiAccountSystem.repository.UserRepository;
import co.com.icesi.icesiAccountSystem.service.AccountService;

import co.com.icesi.icesiAccountSystem.unit.service.matcher.IcesiAccountMatcher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

public class AccountServiceTest {
    private AccountService accountService;
    private AccountRepository accountRepository;
    private UserRepository userRepository;
    private AccountMapper accountMapper;

    @BeforeEach
    private void init(){
        accountRepository = mock(AccountRepository.class);
        userRepository = mock(UserRepository.class);
        accountMapper=spy(AccountMapperImpl.class);
        accountService = new AccountService(accountRepository, userRepository, accountMapper);
    }

    @Test
    public void testCreateAccount_HappyPath(){
        // Arrange
        var user= defaultIcesiUser();
        var accountDTO = defaultNormalAccountDTO();
        when(userRepository.findByEmail(any())).thenReturn(Optional.of(user));
        // Act
        accountService.saveAccount(accountDTO);
        // Assert
        IcesiAccount newIcesiAccount = IcesiAccount.builder()
                .accountId(UUID.randomUUID())
                .accountNumber("025-456829-89")
                .user(defaultIcesiUser())
                .balance(1300000)
                .type(AccountType.NORMAL)
                .active(true)
                .build();
        verify(accountRepository,times(1)).save(argThat(new IcesiAccountMatcher(newIcesiAccount)));
        verify(accountMapper, times(1)).fromAccountDTO(any());
        verify(accountMapper, times(1)).fromAccountToResponseAccountDTO(any());
    }

    @Test
    public void testCreateAccountWithoutUserEmail(){
        try {
            // Act
            accountService.saveAccount(
                    RequestAccountDTO.builder()
                            .userEmail("")
                            .balance(520000)
                            .type("deposit only")
                            .build()
            );
            fail();
        } catch (RuntimeException exception){
            String message = exception.getMessage();
            // Assert
            assertEquals("The email of the user was not specified or user does not exists yet, it is not possible to create an account without a user.", message);
        }
    }

    @Test
    public void testCreateAccountWithUserThatDoesNotExists(){
        try {
            // Act
            accountService.saveAccount(
                    RequestAccountDTO.builder()
                            .userEmail("ykaar@gmail.com")
                            .balance(520000)
                            .type("deposit only")
                            .build()
            );
            fail();
        } catch (RuntimeException exception){
            String message = exception.getMessage();
            // Assert
            assertEquals("The email of the user was not specified or user does not exists yet, it is not possible to create an account without a user.", message);
        }
    }

    @Test
    public void testCreateAccountWithBalanceBelowCero(){
        // Arrange
        var user = defaultIcesiUser();
        when(userRepository.findByEmail(any())).thenReturn(Optional.of(user));
        try {
            // Act
            accountService.saveAccount(
                    RequestAccountDTO.builder()
                            .userEmail("ykaar@gmail.com")
                            .balance(-100000)
                            .type("normal")
                            .build()
            );
            fail();
        } catch (RuntimeException exception){
            String message = exception.getMessage();
            // Assert
            assertEquals("Account's balance can not be below 0.", message);
        }
    }

    @Test
    public void testCreateAccountWithoutType(){
        // Arrange
        var user = defaultIcesiUser();
        when(userRepository.findByEmail(any())).thenReturn(Optional.of(user));
        try {
            // Act
            accountService.saveAccount(
                    RequestAccountDTO.builder()
                            .userEmail("ykaar@gmail.com")
                            .balance(520000)
                            .type("")
                            .build()
            );
            fail();
        } catch (RuntimeException exception){
            String message = exception.getMessage();
            // Assert
            assertEquals("Account's type can not be empty.", message);
        }
    }

    @Test
    public void testCreateAccountWithTypeThatDoesNotExists(){
        // Arrange
        var user = defaultIcesiUser();
        when(userRepository.findByEmail(any())).thenReturn(Optional.of(user));
        try {
            // Act
            accountService.saveAccount(
                    RequestAccountDTO.builder()
                            .userEmail("ykaar@gmail.com")
                            .balance(520000)
                            .type("current")
                            .build()
            );
            fail();
        } catch (RuntimeException exception){
            String message = exception.getMessage();
            // Assert
            assertEquals("Account's type has to be deposit only or normal.", message);

        }
    }

    @Test
    public void testAccountNumber(){
        // Arrange
        var user = defaultIcesiUser();
        var accountDTO = defaultDepositOnlyAccountDTO();
        when(userRepository.findByEmail(any())).thenReturn(Optional.of(user));
        // Act
        accountService.saveAccount(accountDTO);
        String accNumPattern = "[0-9]{3}-[0-9]{6}-[0-9]{2}";
        // Assert
        verify(accountRepository,times(1)).save(argThat(accNum -> accNum.getAccountNumber().matches(accNumPattern)));
    }

    @Test
    public void testDisableAccount_HappyPath(){
        // Arrange
        var user = defaultIcesiUser();
        when(userRepository.findByEmail(any())).thenReturn(Optional.of(user));

        IcesiAccount account = defaultIcesiNormalAccount1();
        account.setBalance(0);
        when(accountRepository.findByAccountNumber(any())).thenReturn(Optional.of(account));

        // Act
        ResponseAccountDTO responseAccountDTO = accountService.disableAccount(account.getAccountNumber());

        // Assert
        assertFalse(responseAccountDTO.isActive());
        verify(accountRepository, times(1)).findByAccountNumber(any());
        verify(accountRepository, times(1)).save(any());
        verify(accountMapper, times(1)).fromAccountToResponseAccountDTO(any());
    }

    @Test
    public void testDisableAccountWhenBalanceIsNotZero(){
        // Arrange
        var user = defaultIcesiUser();
        var icesiAccount = defaultIcesiNormalAccount1();
        when(userRepository.findByEmail(any())).thenReturn(Optional.of(user));
        when(accountRepository.findByAccountNumber(any())).thenReturn(Optional.of(icesiAccount));
        try {
            // Act
            accountService.disableAccount(defaultIcesiNormalAccount1().getAccountNumber());
            fail();
        } catch (RuntimeException exception){
            String message = exception.getMessage();
            // Assert
            assertEquals("An account can only be disabled if the balance is 0.", message);
        }
    }

    @Test
    public void testDisableAccountThatDoesNotExists(){
        // Arrange
        String accNumber = "025-456829-89";
        try {
            // Act
            accountService.disableAccount(accNumber);
            fail();
        } catch (RuntimeException exception){
            String message = exception.getMessage();
            // Assert
            assertEquals("There is not an account with the entered number.", message);
        }
    }

    @Test
    public void testEnableAccount_HappyPath(){
        // Arrange
        var user = defaultIcesiUser();
        when(userRepository.findByEmail(any())).thenReturn(Optional.of(user));

        IcesiAccount account = defaultIcesiDepositOnlyAccount();
        account.setActive(false);
        when(accountRepository.findByAccountNumber(any())).thenReturn(Optional.of(account));

        // Act
        ResponseAccountDTO responseAccountDTO = accountService.enableAccount(account.getAccountNumber());

        // Assert
        assertTrue(responseAccountDTO.isActive());
        verify(accountRepository, times(1)).findByAccountNumber(any());
        verify(accountRepository, times(1)).save(any());
        verify(accountMapper, times(1)).fromAccountToResponseAccountDTO(any());

    }

    @Test
    public void testEnableAccountThatDoesNotExists(){
        // Arrange
        String accNumber = "025-456829-89";
        try {
            // Act
            accountService.enableAccount(accNumber);
            fail();
        } catch (RuntimeException exception){
            String message = exception.getMessage();
            // Assert
            assertEquals("There is not an account with the entered number.", message);
        }
    }

    @Test
    public void testWithdrawMoney_HappyPath(){
        // Arrange
        var user = defaultIcesiUser();
        var icesiAccount = defaultIcesiNormalAccount1();
        when(userRepository.findByEmail(any())).thenReturn(Optional.of(user));
        when(accountRepository.findByAccountNumber(any())).thenReturn(Optional.of(icesiAccount));

        // Act
        TransactionOperationDTO transactionOperationDTO = accountService.withdrawMoney(defaultTransactionOperationDTO1());

        // Assert
        assertEquals(transactionOperationDTO.getResult(), "The withdrawal of money from the account was successful");
        assertTrue(accountRepository.findByAccountNumber(transactionOperationDTO.getAccountFrom()).get().getBalance()==800000);
    }

    @Test
    public void testWithdrawMoneyWhenTheAccountDoesNotExists(){
        // Arrange
        var accountDTO = defaultTransactionOperationDTO2();
        try {
            // Act
            accountService.withdrawMoney(accountDTO);
            fail();
        } catch (RuntimeException exception){
            String message = exception.getMessage();
            // Assert
            assertEquals("There is not an account with the entered number.", message);
        }

    }

    @Test
    public void testWithdrawMoneyWhenAmountIsGreaterThanBalance(){
        // Arrange
        var user = defaultIcesiUser();
        var icesiAccount = defaultIcesiNormalAccount1();
        when(userRepository.findByEmail(any())).thenReturn(Optional.of(user));
        when(accountRepository.findByAccountNumber(any())).thenReturn(Optional.of(icesiAccount));

        TransactionOperationDTO transaction = defaultTransactionOperationDTO1();
        transaction.setAmount(1600000L);

        try {
            // Act
            accountService.withdrawMoney(transaction);
            fail();
        } catch (RuntimeException exception){
            String message = exception.getMessage();
            // Assert
            assertEquals("Insufficient funds.", message);
        }
    }

    @Test
    public void testWithdrawMoneyWhenTheAccountIsDisabled(){
        // Arrange
        var user = defaultIcesiUser();
        when(userRepository.findByEmail(any())).thenReturn(Optional.of(user));

        IcesiAccount account = defaultIcesiDepositOnlyAccount();
        account.setActive(false);
        when(accountRepository.findByAccountNumber(any())).thenReturn(Optional.of(account));

        try {
            // Act
            accountService.withdrawMoney(defaultTransactionOperationDTO2());
            fail();
        } catch (RuntimeException exception){
            String message = exception.getMessage();
            // Assert
            assertEquals("The account to/from which you want to make a transaction is disabled.", message);
        }

    }

    @Test
    public void testDepositMoney_HappyPath(){
        // Arrange
        var user = defaultIcesiUser();
        var icesiAccount = defaultIcesiNormalAccount1();
        when(userRepository.findByEmail(any())).thenReturn(Optional.of(user));
        when(accountRepository.findByAccountNumber(any())).thenReturn(Optional.of(icesiAccount));

        // Act
        TransactionOperationDTO transactionOperationDTO = accountService.depositMoney(defaultTransactionOperationDTO1());

        // Assert
        assertEquals(transactionOperationDTO.getResult(), "The deposit of money to the account was successful.");
        assertTrue(accountRepository.findByAccountNumber(transactionOperationDTO.getAccountFrom()).get().getBalance()==1800000);
    }


    @Test
    public void testDepositMoneyWhenTheAccountDoesNotExists(){
        // Arrange
        var accountDTO = defaultTransactionOperationDTO2();
        try {
            // Act
            accountService.depositMoney(accountDTO);
            fail();
        } catch (RuntimeException exception){
            String message = exception.getMessage();
            // Assert
            assertEquals("There is not an account with the entered number.", message);
        }

    }

    @Test
    public void testDepositMoneyWhenTheAccountIsDisabled(){
        // Arrange
        var user = defaultIcesiUser();
        when(userRepository.findByEmail(any())).thenReturn(Optional.of(user));

        IcesiAccount account = defaultIcesiDepositOnlyAccount();
        account.setActive(false);
        when(accountRepository.findByAccountNumber(any())).thenReturn(Optional.of(account));

        var accountDTO = defaultTransactionOperationDTO2();

        try {
            // Act
            accountService.depositMoney(accountDTO);
            fail();
        } catch (RuntimeException exception){
            String message = exception.getMessage();
            // Assert
            assertEquals("The account to/from which you want to make a transaction is disabled.", message);
        }

    }

    @Test
    public void testTransferMoney_HappyPath(){
        // Arrange
        var user = defaultIcesiUser();
        var acc1 = defaultIcesiNormalAccount1();
        var acc2 = defaultIcesiNormalAccount2();
        when(userRepository.findByEmail(any())).thenReturn(Optional.of(user));
        when(accountRepository.findByAccountNumber("025-456829-89")).thenReturn(Optional.of(acc1));
        when(accountRepository.findByAccountNumber("019-202301-03")).thenReturn(Optional.of(acc2));
        var accountDTO = defaultTransactionOperationDTO1();

        // Act
        TransactionOperationDTO transactionOperationDTO = accountService.transferMoney(accountDTO);

        // Assert
        assertEquals(transactionOperationDTO.getResult(), "The transfer of money was successful.");
        assertEquals(800000, accountRepository.findByAccountNumber(transactionOperationDTO.getAccountFrom()).get().getBalance());
        assertEquals(1800000,accountRepository.findByAccountNumber(transactionOperationDTO.getAccountTo()).get().getBalance());
    }

    @Test
    public void testTransferMoneyWhenAccountFromDoesNotExists(){
        // Arrange
        var user = defaultIcesiUser();
        var acc2 = defaultIcesiNormalAccount2();
        when(userRepository.findByEmail(any())).thenReturn(Optional.of(user));
        when(accountRepository.findByAccountNumber("019-202301-03")).thenReturn(Optional.of(acc2));

        var accountDTO = defaultTransactionOperationDTO1();

        try {
            // Act
            accountService.transferMoney(accountDTO);
            fail();
        } catch (RuntimeException exception){
            String message = exception.getMessage();
            // Assert
            assertEquals("There is not an account with the entered number.", message);
        }
    }

    @Test
    public void testTransferMoneyWhenAccountToDoesNotExists(){
        // Arrange
        var user = defaultIcesiUser();
        var acc1 = defaultIcesiNormalAccount1();
        when(userRepository.findByEmail(any())).thenReturn(Optional.of(user));
        when(accountRepository.findByAccountNumber("025-456829-89")).thenReturn(Optional.of(acc1));

        var accountDTO = defaultTransactionOperationDTO1();

        try {
            // Act
            accountService.transferMoney(accountDTO);
            fail();
        } catch (RuntimeException exception){
            String message = exception.getMessage();
            // Assert
            assertEquals("There is not an account with the entered number.", message);
        }
    }

    @Test
    public void testTransferMoneyWhenAmountIsGreaterThanAccountsFromBalance(){
        // Arrange
        var user = defaultIcesiUser();
        var acc1 = defaultIcesiNormalAccount1();
        var acc2 = defaultIcesiNormalAccount2();
        when(userRepository.findByEmail(any())).thenReturn(Optional.of(user));
        when(accountRepository.findByAccountNumber("025-456829-89")).thenReturn(Optional.of(acc1));
        when(accountRepository.findByAccountNumber("019-202301-03")).thenReturn(Optional.of(acc2));

        TransactionOperationDTO transaction = defaultTransactionOperationDTO1();
        transaction.setAmount(1600000L);

        try {
            // Act
            accountService.transferMoney(transaction);
            fail();
        } catch (RuntimeException exception){
            String message = exception.getMessage();
            // Assert
            assertEquals("Insufficient funds.", message);
        }
    }

    @Test
    public void testTransferMoneyWhenAccountFromIsDisabled(){
        // Arrange
        var user = defaultIcesiUser();
        var acc2 = defaultIcesiNormalAccount2();
        when(userRepository.findByEmail(any())).thenReturn(Optional.of(user));
        IcesiAccount accountFrom = defaultIcesiNormalAccount1();
        accountFrom.setActive(false);
        when(accountRepository.findByAccountNumber("025-456829-89")).thenReturn(Optional.of(accountFrom));
        when(accountRepository.findByAccountNumber("019-202301-03")).thenReturn(Optional.of(acc2));

        var accountDTO = defaultTransactionOperationDTO1();

        try {
            // Act
            accountService.transferMoney(accountDTO);
            fail();
        } catch (RuntimeException exception){
            String message = exception.getMessage();
            // Assert
            assertEquals("The account to/from which you want to make a transaction is disabled.", message);
        }

    }

    @Test
    public void testTransferMoneyWhenAccountToIsDisabled(){
        // Arrange
        var user = defaultIcesiUser();
        var acc1 = defaultIcesiNormalAccount1();
        when(userRepository.findByEmail(any())).thenReturn(Optional.of(user));
        when(accountRepository.findByAccountNumber("025-456829-89")).thenReturn(Optional.of(acc1));
        IcesiAccount accountTo = defaultIcesiNormalAccount2();
        accountTo.setActive(false);
        when(accountRepository.findByAccountNumber("019-202301-03")).thenReturn(Optional.of(accountTo));

        var accountDTO = defaultTransactionOperationDTO1();

        try {
            // Act
            accountService.transferMoney(accountDTO);
            fail();
        } catch (RuntimeException exception){
            String message = exception.getMessage();
            // Assert
            assertEquals("The account to/from which you want to make a transaction is disabled.", message);
        }

    }

    @Test
    public void testTransferMoneyWhenAccountFromIsDepositOnly(){
        // Arrange
        var user = defaultIcesiUser();
        var acc2 = defaultIcesiNormalAccount2();
        when(userRepository.findByEmail(any())).thenReturn(Optional.of(user));
        IcesiAccount accountFrom = defaultIcesiNormalAccount1();
        accountFrom.setType(AccountType.DEPOSIT_ONLY);
        when(accountRepository.findByAccountNumber("025-456829-89")).thenReturn(Optional.of(accountFrom));
        when(accountRepository.findByAccountNumber("019-202301-03")).thenReturn(Optional.of(acc2));

        var accountDTO = defaultTransactionOperationDTO1();

        try {
            // Act
            accountService.transferMoney(accountDTO);
            fail();
        } catch (RuntimeException exception){
            String message = exception.getMessage();
            // Assert
            assertEquals("The source or destination account is marked as deposit only, it can't transfer or be transferred money, only withdrawal and deposit.", message);
        }

    }

    @Test
    public void testTransferMoneyWhenAccountToIsDepositOnly(){
        // Arrange
        var user = defaultIcesiUser();
        var acc2 = defaultIcesiNormalAccount2();
        when(userRepository.findByEmail(any())).thenReturn(Optional.of(user));
        when(accountRepository.findByAccountNumber("025-456829-89")).thenReturn(Optional.of(acc2));
        IcesiAccount accountTo = defaultIcesiNormalAccount1();
        accountTo.setType(AccountType.DEPOSIT_ONLY);
        when(accountRepository.findByAccountNumber("019-202301-03")).thenReturn(Optional.of(accountTo));

        var accountDTO = defaultTransactionOperationDTO1();

        try {
            // Act
            accountService.transferMoney(accountDTO);
            fail();
        } catch (RuntimeException exception){
            String message = exception.getMessage();
            // Assert
            assertEquals("The source or destination account is marked as deposit only, it can't transfer or be transferred money, only withdrawal and deposit.", message);
        }

    }

    private TransactionOperationDTO defaultTransactionOperationDTO1(){
        return TransactionOperationDTO.builder()
                .result("")
                .accountFrom("025-456829-89")
                .accountTo("019-202301-03")
                .amount(500000L)
                .build();
    }

    private TransactionOperationDTO defaultTransactionOperationDTO2(){
        return TransactionOperationDTO.builder()
                .result("")
                .accountFrom("025-253568-54")
                .accountTo("025-456829-89")
                .amount(25250000L)
                .build();
    }

    private RequestAccountDTO defaultNormalAccountDTO() {
        return RequestAccountDTO.builder()
                .userEmail(defaultIcesiUser().getEmail())
                .balance(1300000)
                .type("normal")
                .build();
    }

    private IcesiAccount defaultIcesiNormalAccount1(){
        return IcesiAccount.builder()
                .accountId(UUID.randomUUID())
                .accountNumber("025-456829-89")
                .user(defaultIcesiUser())
                .balance(1300000)
                .type(AccountType.NORMAL)
                .active(true)
                .build();
    }

    private IcesiAccount defaultIcesiNormalAccount2(){
        return IcesiAccount.builder()
                .accountId(UUID.randomUUID())
                .accountNumber("019-202301-03")
                .user(defaultIcesiUser())
                .balance(1300000)
                .type(AccountType.NORMAL)
                .active(true)
                .build();
    }

    private RequestAccountDTO defaultDepositOnlyAccountDTO() {
        return RequestAccountDTO.builder()
                .userEmail(defaultIcesiUser().getEmail())
                .balance(50000000)
                .type("deposit only")
                .build();
    }

    private IcesiAccount defaultIcesiDepositOnlyAccount(){
        return IcesiAccount.builder()
                .accountId(UUID.randomUUID())
                .accountNumber("025-253568-54")
                .user(defaultIcesiUser())
                .balance(50000000)
                .type(AccountType.DEPOSIT_ONLY)
                .active(true)
                .build();
    }

    private IcesiUser defaultIcesiUser(){
        return IcesiUser.builder()
                .userId(UUID.randomUUID())
                .role(defaultIcesiRole())
                .firstName("Damiano")
                .lastName("David")
                .email("ykaar@gmail.com")
                .phoneNumber("3152485689")
                .password("taEkbs08")
                .build();
    }

    private IcesiRole defaultIcesiRole() {
        return IcesiRole.builder()
                .roleId(UUID.randomUUID())
                .description("Director del programa de Ingenieria de sistemas")
                .name("Director SIS")
                .build();
    }

}