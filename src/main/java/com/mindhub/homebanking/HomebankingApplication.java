package com.mindhub.homebanking;

import com.mindhub.homebanking.models.Account;
import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.repositories.AccountRepository;
import com.mindhub.homebanking.repositories.ClientRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.LocalDate;

@SpringBootApplication
public class HomebankingApplication {

	public static void main(String[] args) {
		SpringApplication.run(HomebankingApplication.class, args);
	}

	@Bean
	public CommandLineRunner initData(ClientRepository clientRepository, AccountRepository accountRepository) {

		LocalDate today = LocalDate.now();

		return (args) -> {
			// save a couple of clients
			Client melba = new Client("Melba", "Morel", "melba@mindhub.com");
			clientRepository.save(melba);

			Account melbaAcc1 = new Account("VIN001", today , 5000);
			Account melbaAcc2 = new Account("VIN002", today.plusDays(1) , 7500);
			melba.addAccount(melbaAcc1);
			melba.addAccount(melbaAcc2);
			accountRepository.save(melbaAcc1);
			accountRepository.save(melbaAcc2);
			clientRepository.save(melba);

		};
	}

}
