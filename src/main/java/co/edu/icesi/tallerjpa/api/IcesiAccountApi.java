package co.edu.icesi.tallerjpa.api;

import co.edu.icesi.tallerjpa.dto.*;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@RequestMapping("/icesi_accounts")
public interface IcesiAccountApi {
    public static final String ROOT_PATH = "/icesi_accounts";
    @PostMapping
    public IcesiAccountShowDTO createIcesiAccount(@Valid @RequestBody IcesiAccountCreateDTO icesiAccountCreateDTO);

    @PatchMapping("enable/{accountId}")
    public IcesiAccountShowDTO enableAccount(@PathVariable("accountId") String accountId);

    @PatchMapping("disable/{accountId}")
    public IcesiAccountShowDTO disableAccount(@PathVariable("accountId") String accountId);

    @PatchMapping("withdrawal_money")
    public TransactionResultDTO withdrawalMoney(@Valid @RequestBody TransactionCreateDTO transactionCreateDTO);

    @PatchMapping("deposit_money")
    public TransactionResultDTO depositMoney(@Valid @RequestBody TransactionCreateDTO transactionCreateDTO);

    @PatchMapping("transfer_money")
    public TransactionResultDTO transferMoney(@Valid @RequestBody TransactionCreateDTO transactionCreateDTO);

    @GetMapping("id/{accountId}")
    public IcesiAccountShowDTO getAccountByAccountNumber(@PathVariable("accountId") String accountId);

}
