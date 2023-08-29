package com.mindhub.homebanking.controllers;

import com.mindhub.homebanking.dto.AccountDTO;
import com.mindhub.homebanking.models.Account;
import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.repositories.AccountRepository;
import com.mindhub.homebanking.repositories.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Random;

import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping("/api")
public class AccountController {

    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private ClientRepository clientRepository;

    @GetMapping("/accounts")
    public List<AccountDTO> getAccounts() {
        return accountRepository.findAll().stream().map(account -> new AccountDTO(account)).collect(toList());
    }

    @RequestMapping("accounts/{id}")
    public AccountDTO getAccount(@PathVariable Long id) {
        return accountRepository.findById(id).map(account -> new AccountDTO(account)).orElse(null);
    }

    @PostMapping("/clients/current/accounts")
    public ResponseEntity<Object> registerAccount(Authentication authentication) {
        Client authClient = clientRepository.findByEmail(authentication.getName());
        if (authClient != null) {
            List<Account> accounts = accountRepository.findByClient(authClient);
            if (accounts.size() >= 3) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Clients cannot have more than 3 accounts");
            } else {
                Random random = new Random();
                boolean accountNumberExists;
                String accountNumber;

                do {
                    int number = random.nextInt(900000) + 100000;
                    accountNumber = "VIN-" + number;
                    String finalAccountNumber = accountNumber;
                    accountNumberExists = clientRepository.findAll().stream()
                            .anyMatch(client -> client.getAccounts().stream()
                                    .anyMatch(account -> account.getNumber().equals(finalAccountNumber)));

                    if (!accountNumberExists) {
                        Account newAccount = new Account(finalAccountNumber, LocalDate.now(), 0.0);
                        authClient.addAccount(newAccount);
                        accountRepository.save(newAccount);
                    }
                } while (accountNumberExists);

                return ResponseEntity.status(HttpStatus.CREATED).body("Account created successfully");
            }

        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid client");
        }
    }
}
