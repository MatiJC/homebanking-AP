package com.mindhub.homebanking;

import com.mindhub.homebanking.models.Account;
import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.models.Transaction;
import com.mindhub.homebanking.models.TransactionType;
import com.mindhub.homebanking.repositories.AccountRepository;
import com.mindhub.homebanking.repositories.ClientRepository;
import com.mindhub.homebanking.repositories.TransactionRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.LocalDate;
import java.time.LocalDateTime;

@SpringBootApplication
public class HomebankingApplication {

	public static void main(String[] args) {
		SpringApplication.run(HomebankingApplication.class, args);
	}

	@Bean
	public CommandLineRunner initData(ClientRepository clientRepository, AccountRepository accountRepository,
									  TransactionRepository transactionRepository) {

		LocalDate today = LocalDate.now();
		LocalDateTime now = LocalDateTime.now();

		return (args) -> {
			Client melba = new Client("Melba", "Morel", "melba@mindhub.com");

			Account melbaAcc1 = new Account("VIN001", today , 5000, melba);
			Account melbaAcc2 = new Account("VIN002", today.plusDays(1) , 7500, melba);

			Transaction order1 = new Transaction(TransactionType.DEBIT, -3000, "Zapatillas", now, melbaAcc1);
			Transaction order2 = new Transaction(TransactionType.CREDIT, 5000, "Transferencia", now, melbaAcc1);


			clientRepository.save(melba);
			melba.addAccount(melbaAcc1);
			melba.addAccount(melbaAcc2);
			accountRepository.save(melbaAcc1);
			accountRepository.save(melbaAcc2);
			melbaAcc1.addTransaction(order1);
			melbaAcc1.addTransaction(order2);
			transactionRepository.save(order1);
			transactionRepository.save(order2);

		};
	}

}
