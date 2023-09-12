package com.mindhub.homebanking.services;

import com.mindhub.homebanking.dto.ClientDTO;
import com.mindhub.homebanking.models.Client;

import java.util.List;

public interface ClientService {
    List<ClientDTO> getClients();
    void saveClient(Client client);
    Client getClientById(Long id);
    Client getClientByEmail(String email);
    ClientDTO getClient(Client client);
    ClientDTO getCurrentClient(String email);
    Client createClient(String firstName, String lastName, String email, String password);

}
