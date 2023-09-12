package com.mindhub.homebanking.controllers;

import com.mindhub.homebanking.dto.LoanApplicationDTO;
import com.mindhub.homebanking.dto.LoanDTO;
import com.mindhub.homebanking.models.*;
import com.mindhub.homebanking.services.*;
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
public class LoanController {

    @Autowired
    private LoanService loanService;
    @Autowired
    private AccountService accountService;
    @Autowired
    private ClientLoanService clientLoanService;
    @Autowired
    private ClientService clientService;
    @Autowired
    private TransactionService transactionService;

    @GetMapping("/loans")
    public List<LoanDTO> getLoans() {
        return loanService.getLoans();
    }

    @Transactional
    @PostMapping("/loans")
    public ResponseEntity<Object> requestLoan(@RequestBody LoanApplicationDTO loanApplicationDTO,
                                              Authentication authentication) {
        boolean isClientAuthenticated = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority).anyMatch(authority -> authority.equals("CLIENT"));
        if (isClientAuthenticated) {
            if (loanApplicationDTO.getAmount() <= 0) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Amount cannot be 0 or less");
            }
            if (loanApplicationDTO.getPayments() <= 0) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Fees to pay cannot be 0 or less");
            }

            Client authClient = clientService.getClientByEmail(authentication.getName());

            Loan loan = loanService.getLoanById(loanApplicationDTO.getId());

            if (loanApplicationDTO.getAmount() > loan.getAmount()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Loan requested amount exceeds the maximum permitted");
            }

            if (!loan.getPayments().contains(loanApplicationDTO.getPayments())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid number of payments");
            }

            Account destinationAccount = accountService.getAccountByNumber(loanApplicationDTO.getToAccountNumber());

            if (destinationAccount == null) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Destination account does not exist");
            }

            List<ClientLoan> loansAvailable = clientLoanService.getClientLoanByEmailAndName(authentication.getName(), loan.getName());

            if (!loansAvailable.isEmpty()){
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You already have a loan with that name");
            }

            List<Account> clientAccounts = accountService.getClientAccounts(authClient);

            if (!clientAccounts.contains(destinationAccount)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("This account does not belong to the logged client");
            }

            double amountWithInterest = loanApplicationDTO.getAmount() * 1.20;

            ClientLoan loanRequested = clientLoanService.createClientLoan(amountWithInterest, loanApplicationDTO.getPayments(), authClient, loan);

            authClient.addClientLoan(loanRequested);

            Transaction loanTransaction = transactionService.createTransaction(TransactionType.CREDIT,
                    amountWithInterest, "Approved loan: " + loan.getName(), LocalDateTime.now());

            destinationAccount.setBalance(destinationAccount.getBalance() + loanRequested.getAmount());

            clientLoanService.saveClientLoan(loanRequested);
            transactionService.saveTransaction(loanTransaction);
            accountService.saveAccount(destinationAccount);
            clientService.saveClient(authClient);

            return ResponseEntity.status(HttpStatus.CREATED).body("Loan requested successfully");

        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authentication required");
        }
    }
}
