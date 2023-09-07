package com.mindhub.homebanking.controllers;

import com.mindhub.homebanking.dto.TransactionDTO;
import com.mindhub.homebanking.models.Account;
import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.models.Transaction;
import com.mindhub.homebanking.models.TransactionType;
import com.mindhub.homebanking.services.AccountService;
import com.mindhub.homebanking.services.ClientService;
import com.mindhub.homebanking.services.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api")
public class TransactionController {
    @Autowired
    private TransactionService transactionService;
    @Autowired
    private ClientService clientService;
    @Autowired
    private AccountService accountService;

    @GetMapping("/transactions")
    public List<TransactionDTO> getTransactions() {
        return transactionService.getTransactions();
    }

    @Transactional
    @PostMapping("/transactions")
    public ResponseEntity<Object> createTransaction(@RequestParam String fromAccountNumber,
                                            @RequestParam String toAccountNumber,
                                            @RequestParam long amount,
                                            @RequestParam String description,
                                            Authentication authentication) {

        boolean isClientAuthenticated = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority).anyMatch(authority -> authority.equals("CLIENT"));
        if (!isClientAuthenticated) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Login required");
        }

        Client authClient = clientService.getClientByEmail(authentication.getName());

        if (amount <= 0 || description.isEmpty() || fromAccountNumber.isEmpty() || toAccountNumber.isEmpty() ) {
            String errorMsg = "Missing data: ";
            if (amount <= 0) {
                errorMsg.concat("Amount cannot be 0 or less");
            } else if (description.isEmpty()) {
                errorMsg.concat("Description");
            } else if (fromAccountNumber.isEmpty()) {
                errorMsg.concat("From account number");
            } else if (toAccountNumber.isEmpty()) {
                errorMsg.concat("To account number");
            }
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorMsg);
        }

        Account sourceAccount = accountService.getAccountByNumber(fromAccountNumber);
        Account destinationAccount = accountService.getAccountByNumber(toAccountNumber);

        if (sourceAccount == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Source account does not exist");
        }
        if (destinationAccount == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Destination account does not exist");
        }

        if (fromAccountNumber.equals(toAccountNumber)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("The source account cannot be the same as the destination one");
        }

        String mailSourceAccountClient = sourceAccount.getClient().getEmail();
        if (!authClient.getEmail().equals(mailSourceAccountClient)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("The account you are trying to use does not belong to you.");
        }

        if (sourceAccount.getBalance() < amount) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Insufficient funds");
        }

        Transaction debitTransaction = transactionService.createTransaction(TransactionType.DEBIT, amount, description, LocalDateTime.now());
        Transaction creditTransaction = transactionService.createTransaction(TransactionType.CREDIT, amount, description, LocalDateTime.now());

        transactionService.saveTransaction(debitTransaction);
        transactionService.saveTransaction(creditTransaction);

        sourceAccount.setBalance(sourceAccount.getBalance() - amount);
        destinationAccount.setBalance(destinationAccount.getBalance() + amount);

        accountService.saveAccount(sourceAccount);
        accountService.saveAccount(destinationAccount);

        return ResponseEntity.status(HttpStatus.CREATED).body("Transactions created successfully");
    }
}
