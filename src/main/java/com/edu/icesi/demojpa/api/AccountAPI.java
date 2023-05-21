package com.edu.icesi.demojpa.api;

import com.edu.icesi.demojpa.dto.request.RequestAccountDTO;
import com.edu.icesi.demojpa.dto.request.RequestTransactionDTO;
import com.edu.icesi.demojpa.dto.response.ResponseAccountDTO;
import com.edu.icesi.demojpa.dto.response.ResponseTransactionDTO;
import org.springframework.web.bind.annotation.*;

import java.util.List;

public interface AccountAPI {
    String BASE_ACCOUNT_URL = "/accounts";
    @GetMapping("/{accountNumber}")
    ResponseAccountDTO getAccount(@PathVariable String accountNumber);
    @GetMapping("/getAccounts")
    List<ResponseAccountDTO> getAllAccounts();
    @PostMapping("/createAccount")
    ResponseAccountDTO createAccount(@RequestBody RequestAccountDTO requestAccountDTO);
    @PatchMapping("/enable")
    ResponseAccountDTO enableAccount(@RequestBody RequestAccountDTO account);
    @PatchMapping("/disable")
    ResponseAccountDTO disableAccount(@RequestBody RequestAccountDTO account);
    @PatchMapping("/withdraw")
    ResponseTransactionDTO withdraw(@RequestBody RequestTransactionDTO transaction);
    @PatchMapping("/deposit")
    ResponseTransactionDTO deposit(@RequestBody RequestTransactionDTO transaction);
    @PatchMapping("/transfer")
    ResponseTransactionDTO transfer(@RequestBody RequestTransactionDTO transaction);
}
