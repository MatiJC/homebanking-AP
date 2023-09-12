package com.mindhub.homebanking.controllers;

import com.mindhub.homebanking.dto.CardDTO;
import com.mindhub.homebanking.models.Card;
import com.mindhub.homebanking.models.CardColor;
import com.mindhub.homebanking.models.CardType;
import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.services.CardService;
import com.mindhub.homebanking.services.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class CardController {

    @Autowired
    private CardService cardService;
    @Autowired
    private ClientService clientService;

    @GetMapping("/clients/current/cards")
    public ResponseEntity<Object> getCards(Authentication authentication) {
        boolean isClientAuthenticated = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority).anyMatch(authority -> authority.equals("CLIENT"));
        if (!isClientAuthenticated) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Login required");
        }

        Client authClient = clientService.getClientByEmail(authentication.getName());
        List<Card> cards = cardService.getClientCards(authClient);

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

        boolean isClientAuthenticated = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority).anyMatch(authority -> authority.equals("CLIENT"));
        if (!isClientAuthenticated) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Login required");
        }
        if (cardColor == null || cardType == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Card type and color are required");
        }

        Client authClient = clientService.getClientByEmail(authentication.getName());

        Set<Card> clientCards = authClient.getCards();

        if (cardService.existsCardByColorAndTypeAndClient(cardColor, cardType, authClient)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You already have a card of the same color and type");
        }

        if (clientCards.size() >= 3) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Clients cannot have more than 3 cards");
        } else {
            String cvv;
            String cardNumber;
            do {
                cvv = cardService.generateCvv();
                cardNumber = cardService.generateCardNumber();
            } while (cardService.checkExistance(cardNumber, cvv));

            Card newCard = cardService.createCard(cardType, cardColor, cardNumber, cvv, authClient.toString());
            cardService.saveCard(newCard);
            authClient.addCards(newCard);
            clientService.saveClient(authClient);

            return ResponseEntity.status(HttpStatus.CREATED).body("Card successfully created");
        }
    }


}
