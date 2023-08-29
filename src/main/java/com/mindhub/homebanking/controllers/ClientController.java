package com.mindhub.homebanking.controllers;

import com.mindhub.homebanking.dto.ClientDTO;
import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.repositories.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping("/api")
public class ClientController {

    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

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

            return new ResponseEntity<>(errorMsg, HttpStatus.FORBIDDEN);
        }

        if (clientRepository.findByEmail(email) !=  null) {
            return new ResponseEntity<>("Email already in use", HttpStatus.FORBIDDEN);
        }

        clientRepository.save(new Client(firstName, lastName, email, passwordEncoder.encode(password)));

        return new ResponseEntity<>(HttpStatus.CREATED);

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
