package co.com.icesi.tallerjpa.controller;

import co.com.icesi.tallerjpa.dto.RequestAccountDTO;
import co.com.icesi.tallerjpa.dto.ResponseAccountDTO;
import co.com.icesi.tallerjpa.service.AccountService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @PostMapping("/add/account")
    public ResponseAccountDTO save(@RequestBody RequestAccountDTO account){
       return accountService.save(account);
    }

    @PatchMapping("/withdraw/{accountNumber}")
    public String withdraw(@PathVariable String accountNumber, @RequestBody Long amount){
        return accountService.withdraw(amount, accountNumber);
    }

    @PatchMapping("/deposit/{accountNumber}")
    public String deposit(@PathVariable String accountNumber, @RequestBody Long amount){
        return accountService.deposit(amount, accountNumber);
    }

    @PatchMapping("/transfer/{accountNumberOrigin}/{accountNumberDestination}")
    public String transfer(@PathVariable String accountNumberOrigin, @PathVariable String accountNumberDestination, @RequestBody Long amount){
        return accountService.transfer(amount, accountNumberOrigin, accountNumberDestination);
    }

    @PatchMapping("/enableAccount/{accountNumber}")
    public String enableAccount(@PathVariable String accountNumber){
        return accountService.enableAccount(accountNumber);
    }

    @PatchMapping("/disableAccount/{accountNumber}")
    public String disableAccount(@PathVariable String accountNumber){
        return accountService.disableAccount(accountNumber);
    }
}