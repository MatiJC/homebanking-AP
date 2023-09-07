package com.mindhub.homebanking.controllers;

import com.mindhub.homebanking.dto.AccountDTO;
import com.mindhub.homebanking.models.Account;
import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.services.AccountService;
import com.mindhub.homebanking.services.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class AccountController {

    @Autowired
    private AccountService accountService;
    @Autowired
    private ClientService clientService;

    @GetMapping("/accounts")
    public List<AccountDTO> getAccounts() {
        return accountService.getAccounts();
    }

    @GetMapping("accounts/{id}")
    public ResponseEntity<Object> getAccount(@PathVariable Long id, Authentication authentication) {
        boolean isClientAuthenticated = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority).anyMatch(authority -> authority.equals("CLIENT"));
        if (!isClientAuthenticated) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Login required");
        }
        Client authClient = clientService.getClientByEmail(authentication.getName());

        Account account = accountService.getAccountById(id);

        if (account == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Account not found");
        }

        if (account.getClient().equals(authClient)) {
            AccountDTO accountDTO = new AccountDTO(account);
            return ResponseEntity.ok(accountDTO);
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You cannot access to an account you do not own");
        }
    }

    @GetMapping("clients/current/accounts")
    public ResponseEntity<Object> getCurrentClientAccounts(Authentication authentication) {
        boolean isClientAuthenticated = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority).anyMatch(authority -> authority.equals("CLIENT"));
        if (!isClientAuthenticated) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Login required");
        }

        Client authClient = clientService.getClientByEmail(authentication.getName());
        List<Account> accounts = accountService.getClientAccounts(authClient);

        if (accounts == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No accounts found");
        }

        List<AccountDTO> accountDTOList = accounts.stream()
                .map(account -> new AccountDTO(account)).collect(Collectors.toList());

        return ResponseEntity.ok(accountDTOList);
    }

    @PostMapping("/clients/current/accounts")
    public ResponseEntity<Object> registerAccount(Authentication authentication) {
        boolean isClientAuthenticated = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority).anyMatch(authority -> authority.equals("CLIENT"));
        if (isClientAuthenticated) {
            Client authClient = clientService.getClientByEmail(authentication.getName());
            List<Account> accounts = accountService.getClientAccounts(authClient);
            if (accounts.size() >= 3) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Clients cannot have more than 3 accounts");
            } else {
                Account account = accountService.createAccount();
                authClient.addAccount(account);
                accountService.saveAccount(account);
            }
            return ResponseEntity.status(HttpStatus.CREATED).body("Account created successfully");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid client");
        }
    }
}
