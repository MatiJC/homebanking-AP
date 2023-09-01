package com.mindhub.homebanking.controllers;

import com.mindhub.homebanking.dto.CardDTO;
import com.mindhub.homebanking.models.Card;
import com.mindhub.homebanking.models.CardColor;
import com.mindhub.homebanking.models.CardType;
import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.repositories.CardRepository;
import com.mindhub.homebanking.repositories.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import com.mindhub.homebanking.utils.generateNumber;

import java.time.LocalDate;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping("/api")
public class CardController {

    @Autowired
    private CardRepository cardRepository;
    @Autowired
    private ClientRepository clientRepository;

    @GetMapping("/clients/current/cards")
    public ResponseEntity<Object> getCards(Authentication authentication) {
        Client authClient = clientRepository.findByEmail(authentication.getName());

        if (authClient == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Login required");
        }

        List<Card> cards = cardRepository.findByClient(authClient);

        if (cards == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No cards found");
        }

        List<CardDTO> cardDTOList = cards.stream().map(card -> new CardDTO(card))
                .collect(Collectors.toList());

        return ResponseEntity.ok(cardDTOList);
    }

    @PostMapping("/clients/current/cards")
    public ResponseEntity<Object> registerCard(Authentication authentication,
                                               @RequestParam CardColor cardColor,
                                               @RequestParam CardType cardType) {
        if (authentication == null || authentication.getName() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Client not authenticated");
        }
        if (cardColor == null || cardType == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Card type and color are required");
        }

        Client authClient = clientRepository.findByEmail(authentication.getName());
        if (authClient != null) {
            Set<Card> clientCards = authClient.getCards();

            if (clientCards.size() >= 3) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Clients cannot have more than 3 cards");
            } else {
                short cvv;
                String cardNumber;
                Random random = new Random();

                do {
                    cvv = (short) (random.nextInt(900) + 100);
                    cardNumber = generateNumber.generateCardNumber();
                } while (cardRepository.existsByCvv(cvv) || cardRepository.existsByNumber(cardNumber));

                Card newCard = new Card(cardType, cardColor, cardNumber, LocalDate.now(), LocalDate.now().plusYears(5), cvv,
                        authClient.toString());
                cardRepository.save(newCard);

                authClient.addCards(newCard);
                clientRepository.save(authClient);

                return ResponseEntity.status(HttpStatus.CREATED).body("Card successfully created");

            }

        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid client");
        }

    }


}
