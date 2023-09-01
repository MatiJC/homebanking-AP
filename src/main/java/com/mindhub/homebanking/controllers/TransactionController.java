package com.mindhub.homebanking.controllers;

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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api")
public class TransactionController {
    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    private AccountRepository accountRepository;

    @Transactional
    @PostMapping("/transactions")
    public ResponseEntity createTransaction(@RequestParam long amount,
                                            @RequestParam String description,
                                            @RequestParam String fromAccount,
                                            @RequestParam String toAccount,
                                            Authentication authentication) {

        if (amount <= 0 || fromAccount.isEmpty() || toAccount.isEmpty()) {
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

        Client authClient = clientRepository.findByEmail(authentication.getName());

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
