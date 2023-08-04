package com.mindhub.homebanking;

import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.repositories.ClientRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class HomebankingApplication {

	public static void main(String[] args) {
		SpringApplication.run(HomebankingApplication.class, args);
	}

	@Bean
	public CommandLineRunner initData(ClientRepository repository) {
		return (args) -> {
			// save a couple of customers
			repository.save(new Client("Melba", "Morel", "melba@mindhub.com"));
			/*repository.save(new Client("Chloe", "O'Brian"));
			repository.save(new Client("Kim", "Bauer"));
			repository.save(new Client("David", "Palmer"));
			repository.save(new Client("Michelle", "Dessler"));*/
		};
	}

}