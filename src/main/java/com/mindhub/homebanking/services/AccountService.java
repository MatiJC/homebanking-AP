package com.mindhub.homebanking.services;

import com.mindhub.homebanking.dto.AccountDTO;
import com.mindhub.homebanking.models.Account;
import com.mindhub.homebanking.models.Client;

import java.util.List;

public interface AccountService {
    List<AccountDTO> getAccounts();
    void saveAccount(Account account);
    Account getAccountByNumber(String number);
    Account getAccountById(Long id);
    AccountDTO getAccount(Account account);
    List<Account> getClientAccounts(Client client);
    Account createAccount();
    boolean existsByNumber(String accountNumber);
    String createAccountNumber();
}
