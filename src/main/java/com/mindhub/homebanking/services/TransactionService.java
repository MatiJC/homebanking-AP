package com.mindhub.homebanking.services;

import com.mindhub.homebanking.dto.TransactionDTO;
import com.mindhub.homebanking.models.Transaction;
import com.mindhub.homebanking.models.TransactionType;

import java.time.LocalDateTime;
import java.util.List;

public interface TransactionService {
    List<TransactionDTO> getTransactions();
    void saveTransaction(Transaction transaction);
    Transaction createTransaction(TransactionType type, double amount, String description, LocalDateTime date);
}
