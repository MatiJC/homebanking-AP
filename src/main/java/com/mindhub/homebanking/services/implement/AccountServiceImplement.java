package com.mindhub.homebanking.services.implement;

import com.mindhub.homebanking.dto.AccountDTO;
import com.mindhub.homebanking.models.Account;
import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.repositories.AccountRepository;
import com.mindhub.homebanking.services.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
public class AccountServiceImplement implements AccountService {

    @Autowired
    private AccountRepository accountRepository;

    @Override
    public List<AccountDTO> getAccounts() {
        return accountRepository.findAll().stream().map(account -> new AccountDTO(account)).collect(toList());
    }

    @Override
    public void saveAccount(Account account) {
        accountRepository.save(account);
    }

    @Override
    public Account getAccountByNumber(String number) {
        return accountRepository.findByNumber(number);
    }

    @Override
    public Account getAccountById(Long id) {
        return accountRepository.findById(id).orElse(null);
    }

    @Override
    public AccountDTO getAccount(Account account) {
        return new AccountDTO(account);
    }

    @Override
    public List<Account> getClientAccounts(Client client) {
        return accountRepository.findByClient(client);
    }

    @Override
    public Account createAccount() {
        return new Account(createAccountNumber(), LocalDate.now(), 0.0);
    }

    @Override
    public boolean existsByNumber(String accountNumber) {
        return accountRepository.existsByNumber(accountNumber);
    }

    @Override
    public String createAccountNumber() {
        String accountNumber;
        do {
            int number = (int) (Math.random() * 999999);
            String strNum = String.format("%06d", number);
            accountNumber = "VIN-" + strNum;
        } while (this.existsByNumber(accountNumber));
        return accountNumber;
    }
}
