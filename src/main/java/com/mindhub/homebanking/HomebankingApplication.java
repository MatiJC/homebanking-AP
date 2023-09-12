package com.mindhub.homebanking;

import com.mindhub.homebanking.models.*;
import com.mindhub.homebanking.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@SpringBootApplication
public class HomebankingApplication {

	@Autowired
	private PasswordEncoder passwordEncoder;

	public static void main(String[] args) {
		SpringApplication.run(HomebankingApplication.class, args);
	}

	@Bean
	public CommandLineRunner initData(ClientRepository clientRepository, AccountRepository accountRepository,
									  TransactionRepository transactionRepository, LoanRepository loanRepository,
									  ClientLoanRepository clientLoanRepository, CardRepository cardRepository) {

		LocalDate today = LocalDate.now();
		LocalDateTime now = LocalDateTime.now();

		return (args) -> {
			/*Client melba = new Client("Melba", "Morel", "melba@mindhub.com",
					passwordEncoder.encode("melba123"));

			Client mati = new Client("Mati", "Cuad", "mcuad@mail.com",
					passwordEncoder.encode("mati123"));

			Client admin = new Client("Admin", "Admin", "admin@mh.com",
					passwordEncoder.encode("admin123"));

			clientRepository.save(admin);

			Account melbaAcc1 = new Account("VIN-001", today , 5000);
			Account melbaAcc2 = new Account("VIN-002", today.plusDays(1), 7500);
			Account matiAcc1 = new Account("VIN-003", today, 20000);
			Account matiAcc2 = new Account("VIN-004", today.plusDays(1), 15000);

			Transaction order1 = new Transaction(TransactionType.DEBIT, 3000, "Zapatillas", now);
			Transaction order2 = new Transaction(TransactionType.CREDIT, 5000, "Transferencia", now);
			Transaction order3 = new Transaction(TransactionType.DEBIT, 10000, "Supermercado", now);
			Transaction order4 = new Transaction(TransactionType.CREDIT, 11000, "Aguinaldo", now);

			Card card1 = new Card(CardType.DEBIT, CardColor.GOLD, "2746-3765-9034-3087",  today, today.plusYears(5), "156",
					melba.toString());
			Card card2 = new Card(CardType.CREDIT, CardColor.TITANIUM, "0654-3456-7812-7690", today, today.plusYears(5), "943",
					melba.toString());
			Card card3 = new Card(CardType.CREDIT, CardColor.SILVER,"0237-6591-9357-5410",  today, today.plusYears(5), "267",
					mati.toString());


			List<Integer> mortgagePay = new ArrayList<>() {{
                add(12);
                add(24);
                add(36);
                add(48);
                add(60);

            }};
			List<Integer> personalPay = new ArrayList<>() {{
                add(6);
                add(12);
                add(24);
            }};
			List<Integer> automotivePay = new ArrayList<>() {{
                add(6);
                add(12);
                add(24);
                add(36);
            }};

			Loan mortgage = new Loan("Mortgage", 500000, mortgagePay);
			Loan personal = new Loan("Personal", 100000, personalPay);
			Loan automotive = new Loan("Automotive", 300000, automotivePay);


			ClientLoan clientLoan1 = new ClientLoan(400000, 60, melba, mortgage);
			ClientLoan clientLoan2 = new ClientLoan(50000, 12, melba, personal);
			ClientLoan clientLoan3 = new ClientLoan(100000, 24, mati, personal);
			ClientLoan clientLoan4 = new ClientLoan(200000, 36, mati, automotive);


			clientRepository.save(melba);
			melba.addAccount(melbaAcc1);
			melba.addAccount(melbaAcc2);
			accountRepository.save(melbaAcc1);
			accountRepository.save(melbaAcc2);
			melbaAcc1.addTransaction(order1);
			melbaAcc1.addTransaction(order2);
			transactionRepository.save(order1);
			transactionRepository.save(order2);
			melba.addCards(card1);
			melba.addCards(card2);
			cardRepository.save(card1);
			cardRepository.save(card2);


			clientRepository.save(mati);
			mati.addAccount(matiAcc1);
			mati.addAccount(matiAcc2);
			accountRepository.save(matiAcc1);
			accountRepository.save(matiAcc2);
			matiAcc1.addTransaction(order3);
			matiAcc2.addTransaction(order4);
			transactionRepository.save(order3);
			transactionRepository.save(order4);
			mati.addCards(card3);
			cardRepository.save(card3);


			loanRepository.save(mortgage);
			loanRepository.save(personal);
			loanRepository.save(automotive);


			clientLoanRepository.save(clientLoan1);
			clientLoanRepository.save(clientLoan2);
			clientLoanRepository.save(clientLoan3);
			clientLoanRepository.save(clientLoan4);
		};*/
		};
	}
}

