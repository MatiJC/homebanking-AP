package com.mindhub.homebanking.controllers;

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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.mindhub.homebanking.functions.generateNumber;

import java.time.LocalDate;
import java.util.Random;
import java.util.Set;

@RestController
@RequestMapping("/api")
public class CardController {

    @Autowired
    private CardRepository cardRepository;
    @Autowired
    private ClientRepository clientRepository;

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
                short code;
                String cardNumber;
                Random random = new Random();

                do {
                    code = (short) (random.nextInt(900) + 100);
                    cardNumber = generateNumber.generateCardNumber();
                } while (cardRepository.existsByCode(code) || cardRepository.existsByNumber(cardNumber));

                Card newCard = new Card(cardType, cardNumber, code, LocalDate.now(), LocalDate.now().plusYears(5),
                        authClient.toString(), cardColor);
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
