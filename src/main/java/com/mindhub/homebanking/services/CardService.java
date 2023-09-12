package com.mindhub.homebanking.services;

import com.mindhub.homebanking.models.Card;
import com.mindhub.homebanking.models.CardColor;
import com.mindhub.homebanking.models.CardType;
import com.mindhub.homebanking.models.Client;

import java.util.List;

public interface CardService {
    void saveCard(Card card);
    List<Card> getClientCards(Client client);
    Card createCard(CardType type, CardColor color, String cardHolder, String cardNumber, String cvv);
    boolean existsCardByColorAndTypeAndClient(CardColor color, CardType type, Client client);
    boolean existsCardByNumber(String number);
    boolean existsByCvv(String cvv);
    boolean checkExistance(String number, String cvv);
    String generateCvv();
    String generateCardNumber();

}
