package com.mindhub.homebanking.controllers;

import com.mindhub.homebanking.dto.TransactionDTO;
import com.mindhub.homebanking.models.Account;
import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.models.Transaction;
import com.mindhub.homebanking.models.TransactionType;
import com.mindhub.homebanking.repositories.AccountRepository;
import com.mindhub.homebanking.repositories.ClientRepository;
import com.mindhub.homebanking.repositories.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping("/api")
public class TransactionController {
    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    private AccountRepository accountRepository;

    @GetMapping("/transactions")
    public List<TransactionDTO> getTransactions() {
        return transactionRepository.findAll().stream().map(transaction -> new TransactionDTO(transaction)).collect(toList());
    }

    @Transactional
    @PostMapping("/transactions")
    public ResponseEntity<Object> createTransaction(@RequestParam String fromAccount,
                                            @RequestParam String toAccount,
                                            @RequestParam long amount,
                                            @RequestParam String description,
                                            Authentication authentication) {

        boolean isClientAuthenticated = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority).anyMatch(authority -> authority.equals("CLIENT"));
        if (!isClientAuthenticated) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Login required");
        }

        Client authClient = clientRepository.findByEmail(authentication.getName());

        if (amount <= 0 || description.isEmpty() || fromAccount.isEmpty() || toAccount.isEmpty() ) {
            String errorMsg = "Missing data: ";
            if (amount <= 0) {
                errorMsg.concat("Amount cannot be 0 or less");
            } else if (description.isEmpty()) {
                errorMsg.concat("Description");
            } else if (fromAccount.isEmpty()) {
                errorMsg.concat("From account number");
            } else if (toAccount.isEmpty()) {
                errorMsg.concat("To account number");
            }
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorMsg);
        }

        Account sourceAccount = accountRepository.findByNumber(fromAccount);
        Account destinationAccount = accountRepository.findByNumber(toAccount);

        if (sourceAccount == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Source account does not exist");
        }
        if (destinationAccount == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Destination account does not exist");
        }

        if (fromAccount.equals(toAccount)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("The source account cannot be the same as the destination one");
        }

        String mailSourceAccountClient = sourceAccount.getClient().getEmail();
        if (!authClient.getEmail().equals(mailSourceAccountClient)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("The account you are trying to use does not belong to you.");
        }

        if (sourceAccount.getBalance() < amount) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Insufficient funds");
        }

        Transaction debitTransaction = new Transaction(TransactionType.DEBIT, amount, description, LocalDateTime.now());
        Transaction creditTransaction = new Transaction(TransactionType.CREDIT, amount, description, LocalDateTime.now());

        transactionRepository.save(debitTransaction);
        transactionRepository.save(creditTransaction);

        sourceAccount.setBalance(sourceAccount.getBalance() - amount);
        destinationAccount.setBalance(destinationAccount.getBalance() + amount);

        accountRepository.save(sourceAccount);
        accountRepository.save(destinationAccount);

        return ResponseEntity.status(HttpStatus.CREATED).body("Transactions created successfully");
    }
}
