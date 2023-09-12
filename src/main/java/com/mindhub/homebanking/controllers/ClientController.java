package com.mindhub.homebanking.controllers;

import com.mindhub.homebanking.dto.ClientDTO;
import com.mindhub.homebanking.models.Account;
import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.services.AccountService;
import com.mindhub.homebanking.services.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api")
public class ClientController {

    @Autowired
    private ClientService clientService;
    @Autowired
    private AccountService accountService;

    @GetMapping("/clients")
    public List<ClientDTO> getClients() {
        return clientService.getClients();
    }

    @GetMapping("/clients/{id}")
    public ResponseEntity<Object> getClient(@PathVariable Long id, Authentication authentication) {
        Client authClient = clientService.getClientByEmail(authentication.getName());
        Client client = clientService.getClientById(id);

        if (authClient != null && client != null) {
            if (authClient.getId() == client.getId()) {
                ClientDTO clientDTO = new ClientDTO(client);
                return ResponseEntity.ok(clientDTO);
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not authorized to access this information");
            }
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Client not found");
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

        if (clientService.getClientByEmail(email) !=  null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Email already in use");
        }

        Client newClient = clientService.createClient(firstName, lastName, email, password);
        clientService.saveClient(newClient);

        Account newAccount = accountService.createAccount();
        newClient.addAccount(newAccount);
        accountService.saveAccount(newAccount);

        return ResponseEntity.status(HttpStatus.CREATED).body("Registration successful");
    }

    @GetMapping("/clients/current")
    public ResponseEntity<Object> getCurrentClient(Authentication authentication){
        boolean isClientAuthenticated = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority).anyMatch(authority -> authority.equals("CLIENT"));
        if (!isClientAuthenticated) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Client not found");
        }
        Client authClient = clientService.getClientByEmail(authentication.getName());

        ClientDTO client = new ClientDTO(authClient);
        return ResponseEntity.ok(client);
    }
}
