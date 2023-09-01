package com.mindhub.homebanking.controllers;

import com.mindhub.homebanking.dto.ClientDTO;
import com.mindhub.homebanking.models.Account;
import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.repositories.AccountRepository;
import com.mindhub.homebanking.repositories.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
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

    @GetMapping("/clients/{id}")
    public ResponseEntity<Object> getClient(@PathVariable Long id, Authentication authentication) {
        Client authClient = clientRepository.findByEmail(authentication.getName());

        Optional<Client> optionalClient = clientRepository.findById(id);

        if (authClient != null && optionalClient.isPresent()){
            Client client = optionalClient.get();

            if(authClient.getId() == client.getId()){
                ClientDTO clientDTO = new ClientDTO(client);
                return ResponseEntity.ok(clientDTO);
            }else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not authorized to access this information");
            }
        }

        if (authClient == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Authenticated client not found");
        }else{
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("There is no client with this ID");
        }
    }

    @PostMapping(path = "/clients")
    public ResponseEntity<Object> register(
            @RequestParam String firstName, @RequestParam String lastName,
            @RequestParam String email, @RequestParam String password) {

        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || password.isEmpty()) {
            String errorMsg = "Missing data: ";
            if (firstName.isEmpty()) {
                errorMsg.concat("firstName");
            } else if (lastName.isEmpty()) {
                errorMsg.concat("lastName");
            } else if (email.isEmpty()) {
                errorMsg.concat("email");
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

        return ResponseEntity.status(HttpStatus.CREATED).body("Registration successful");
    }

    @GetMapping("/clients/current")
    public ResponseEntity<Object> getCurrentClient(Authentication authentication){
        Client authClient = clientRepository.findByEmail(authentication.getName());
        if (authClient == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Client not found");
        }
        ClientDTO client = new ClientDTO(authClient);
        return ResponseEntity.ok(client);
    }


}
