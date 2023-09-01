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
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

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

    @GetMapping("accounts/{id}")
    public ResponseEntity<Object> getAccount(@PathVariable Long id, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Login required");
        }
        Client authClient = clientRepository.findByEmail(authentication.getName());

        Optional<Account> optionalAccount = accountRepository.findById(id);

        if (!optionalAccount.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("There is no account with that id");
        }

        Account account = optionalAccount.get();

        if (account.getClient().equals(authClient)) {
            AccountDTO accountDTO = new AccountDTO(account);
            return ResponseEntity.ok(accountDTO);
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You cannot access to an account you do not own");
        }
    }

    @GetMapping("clients/current/accounts/")
    public ResponseEntity<Object> getCurrentClientAccounts(Authentication authentication) {
        Client authClient = clientRepository.findByEmail(authentication.getName());

        if (authClient == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Login required");
        }

        List<Account> accounts = accountRepository.findByClient(authClient);

        if (accounts == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No accounts found");
        }

        List<AccountDTO> accountDTOList = accounts.stream()
                .map(account -> new AccountDTO(account)).collect(Collectors.toList());

        return ResponseEntity.ok(accountDTOList);
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
                        Account account = new Account(finalAccountNumber, LocalDate.now(), 0.0);
                        authClient.addAccount(account);
                        accountRepository.save(account);
                    }
                } while (accountNumberExists);

                return ResponseEntity.status(HttpStatus.CREATED).body("Account created successfully");
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid client");
        }
    }
}
