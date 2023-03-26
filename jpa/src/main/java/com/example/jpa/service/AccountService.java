package com.example.jpa.service;

import com.example.jpa.dto.AccountRequestDTO;
import com.example.jpa.dto.AccountResponseDTO;
import com.example.jpa.dto.InactiveAccountException;
import com.example.jpa.exceptions.AccountNotFoundException;
import com.example.jpa.exceptions.AccountTypeException;
import com.example.jpa.exceptions.BalanceNegativeException;
import com.example.jpa.exceptions.UserNotFoundException;
import com.example.jpa.mapper.AccountMapper;
import com.example.jpa.model.IcesiAccount;
import com.example.jpa.model.IcesiUser;
import com.example.jpa.repository.AccountRepository;
import com.example.jpa.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@AllArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;
    private final UserRepository userRepository;

    public AccountResponseDTO save(AccountRequestDTO accountDTO) {

        IcesiUser icesiUser = getUserOfNewAccount(accountDTO); //User must exist
        IcesiAccount account = accountMapper.fromAccountDTO(accountDTO);
        validateAccountBalance(account, 0); //Account balance must be positive
        account.setId(UUID.randomUUID()); //Account ID is generated by the service
        account.setAccountNumber(validateAccountNumber(generateAccountNumber()));
        account.setActive(true);
        account.setUser(icesiUser);
        //userRepository.findByEmail(accountDTO.getUser().getEmail()).get().getIcesiAccountList().add(account); //Add account to user account list
        accountRepository.save(account);
        return accountMapper.fromAccountToSendAccountDTO(account);
    }

    //Accounts number should have the format xxx-xxxxxx-xx where x are numbers [0-9].
    //Account number is generated by the service
    private String generateAccountNumber() {
        IntStream intStream = new Random().ints(11, 0, 9);
        String randomNumbers = intStream
                .mapToObj(Integer::toString)
                .collect(Collectors.joining());

        return String.format("%s-%s-%s",
                randomNumbers.substring(0, 3),
                randomNumbers.substring(3, 9),
                randomNumbers.substring(9, 11));
    }

    //Validate if the account number is unique
    private String validateAccountNumber(String accountNumber){
        //Accounts number should be unique
        if(accountRepository.findByAccountNumber(accountNumber)){
            return validateAccountNumber(generateAccountNumber());
        }
        return accountNumber;
    }

    //Validate the balance of an account
    private void validateAccountBalance(IcesiAccount account, long amount){
        if (account.getBalance() < amount) {
            throw new BalanceNegativeException("Low balance: " + account.getBalance()
                    + "/n" + "Balance can't be negative");
        }
    }

    //Validate if an account exists
    private IcesiAccount validateAccountExists(String accountNumber){
        if(accountRepository.getByAccountNumber(accountNumber).isPresent()){
            return accountRepository.getByAccountNumber(accountNumber).get();
        }else {
            throw new AccountNotFoundException("Account not found");
        }
    }

    //Enable account if balance is positive
    @Transactional
    public boolean enableAccount(String accountNumber){
        IcesiAccount account = validateAccountExists(accountNumber);
        validateAccountBalance(account, 0);
        account.setActive(true);
        accountRepository.save(account);
        return true;
    }

    //Disable account if balance is 0
    public boolean disableAccount(String accountNumber){
        IcesiAccount account = validateAccountExists(accountNumber);
        if(account.getBalance() == 0){
            account.setActive(false);
            accountRepository.save(account);
            return true;
        }else {
            throw new BalanceNegativeException("The account has a positive balance, it's not recommended to disable it");
        }
    }

    //Deposit money to an account
    @Transactional
    public boolean deposit(String accountNumber, Long amount){
        if(accountRepository.getByAccountNumber(accountNumber).isPresent()){
            IcesiAccount account = accountRepository.getByAccountNumber(accountNumber).get();
            account.setBalance(account.getBalance() + amount);
            accountRepository.save(account);
            return true;
        }else {
            throw new AccountNotFoundException("Account not found");
        }
    }

    //Withdraw money from an account
    @Transactional
    public boolean withdraw(String accountNumber, Long amount){
        IcesiAccount account = validateAccountExists(accountNumber);
        validateAccountBalance(account, amount);
        account.setBalance(account.getBalance() - amount);
        accountRepository.save(account);
        return true;
    }

    /* Transfer money from one account to another, if the accounts are active,
     the balance is positive and the amount is less than the balance, and the type
     of the accounts is different from "deposit" */
    @Transactional
    public boolean transfer(String accountNumberFrom, String accountNumberTo, Long amount){
        IcesiAccount accountFrom = validateAccountExists(accountNumberFrom);
        IcesiAccount accountTo = validateAccountExists(accountNumberTo);
        isEnableToTransfer(accountFrom, accountTo);
        validateAccountBalance(accountFrom, amount);
        accountFrom.setBalance(accountFrom.getBalance() - amount);
        accountTo.setBalance(accountTo.getBalance() + amount);
        accountRepository.save(accountFrom);
        accountRepository.save(accountTo);
        return true;
    }

    private void isEnableToTransfer(IcesiAccount accountFrom, IcesiAccount accountTo){
        if(!(accountFrom.isActive()) || !(accountTo.isActive())) {
            throw new InactiveAccountException("One or both accounts are not active");
        }
        if((accountFrom.getType().equalsIgnoreCase("deposit") ||
                accountTo.getType().equalsIgnoreCase("deposit"))) {
            throw new AccountTypeException("One or both accounts are of type deposit");
        }
    }

    //Get user of a new account
    private IcesiUser getUserOfNewAccount(AccountRequestDTO accountDTO){
        if(userRepository.findByEmail(accountDTO.getUser().getEmail()).isPresent()){
            return userRepository.findByEmail(accountDTO.getUser().getEmail()).get();
        }else {
            throw new UserNotFoundException("User not found");
        }
    }

    //Get all accounts
    public List<AccountResponseDTO> getAccounts(){
        return accountRepository.getAllAccounts().stream().map(accountMapper::fromAccountToSendAccountDTO).collect(Collectors.toList());
    }
}