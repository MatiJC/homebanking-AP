package com.mindhub.homebanking.controllers;

import com.mindhub.homebanking.dto.ClientDTO;
import com.mindhub.homebanking.models.Account;
import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.repositories.AccountRepository;
import com.mindhub.homebanking.repositories.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Random;

import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping("/api")
public class ClientController {

    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private AccountRepository accountRepository;

    @GetMapping("/clients")
    public List<ClientDTO> getClients() {
        return clientRepository.findAll().stream().map(client -> new ClientDTO(client)).collect(toList());
    }

    @RequestMapping("clients/{id}")
    public ClientDTO getClient(@PathVariable Long id){
        return clientRepository.findById(id).map(client -> new ClientDTO(client)).orElse(null);
    }

    @PostMapping(path = "/clients")
    public ResponseEntity<Object> register(
            @RequestParam String firstName, @RequestParam String lastName,
            @RequestParam String email, @RequestParam String password) {

        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || password.isEmpty()) {
            String errorMsg = "Missing data: ";
            if (firstName.isEmpty()) {
                errorMsg.concat("firstName ");
            } else if (lastName.isEmpty()) {
                errorMsg.concat("lastName ");
            } else if (email.isEmpty()) {
                errorMsg.concat("email ");
            } else if (password.isEmpty()) {
                errorMsg.concat("password");
            }

            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorMsg);
        }

        if (clientRepository.findByEmail(email) !=  null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Email already in use");
        }

        Client newClient = new Client(firstName, lastName, email, passwordEncoder.encode(password));

        clientRepository.save(newClient);

        Random random = new Random();
        boolean accountNumberExists;
        String accountNumber;

        do {
            int number = random.nextInt(900000) + 100000;
            accountNumber = "VIN-" + number;
            String finalAccountNumber = accountNumber;
            accountNumberExists = clientRepository.findAll().stream()
                    .anyMatch(client -> client.getAccounts().stream()
                            .anyMatch(account -> account.getNumber().equals(finalAccountNumber)));

            if (!accountNumberExists) {
                Account newAccount = new Account(finalAccountNumber, LocalDate.now(), 0.0);
                newClient.addAccount(newAccount);
                accountRepository.save(newAccount);
            }
        } while (accountNumberExists);

        return ResponseEntity.status(HttpStatus.CREATED).body("Registration successful.");

    }

    @RequestMapping("/clients/current")
    public ClientDTO getCurrentClient(Authentication authentication){
        Client client = clientRepository.findByEmail(authentication.getName());
        if (client == null) {
            throw new ResourceNotFoundException("Client not found");
        }
        return new ClientDTO(client);
    }


}
