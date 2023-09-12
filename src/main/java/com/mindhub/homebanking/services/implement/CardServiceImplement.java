package com.mindhub.homebanking.services.implement;

import com.mindhub.homebanking.models.Card;
import com.mindhub.homebanking.models.CardColor;
import com.mindhub.homebanking.models.CardType;
import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.repositories.CardRepository;
import com.mindhub.homebanking.services.CardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Random;

@Service
public class CardServiceImplement implements CardService {

    @Autowired
    private CardRepository cardRepository;

    @Override
    public void saveCard(Card card) {
        cardRepository.save(card);
    }

    @Override
    public List<Card> getClientCards(Client client) {
        return cardRepository.findByClient(client);
    }

    @Override
    public Card createCard(CardType type, CardColor color, String cardNumber, String cvv, String cardHolder) {
        return new Card(type, color, cardNumber, LocalDate.now(), LocalDate.now().plusYears(5),
                cvv, cardHolder);
    }

    @Override
    public boolean existsCardByColorAndTypeAndClient(CardColor color, CardType type, Client client) {
        return cardRepository.existsCardByColorAndTypeAndClient(color, type, client);
    }

    @Override
    public boolean existsCardByNumber(String number) {
        return cardRepository.existsByNumber(number);
    }

    @Override
    public boolean existsByCvv(String cvv) {
        return cardRepository.existsByCvv(cvv);
    }

    @Override
    public boolean checkExistance(String number, String cvv) {
        return (this.existsCardByNumber(number) || this.existsByCvv(cvv));
    }

    @Override
    public String generateCvv() {
        short cvv = (short) (Math.random() * 999);
        return String.format("%03d", cvv);
    }

    @Override
    public String generateCardNumber() {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i=0;i < 4; i++) {
            int number = random.nextInt(9999);
            sb.append(String.format("%04d", number));
            if(i < 3) {
                sb.append("-");
            }
        }
        return sb.toString();
    }
}
