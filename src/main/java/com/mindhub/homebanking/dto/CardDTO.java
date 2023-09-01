package com.mindhub.homebanking.dto;

import com.mindhub.homebanking.models.Card;
import com.mindhub.homebanking.models.CardColor;
import com.mindhub.homebanking.models.CardType;

import java.time.LocalDate;

public class CardDTO {
    private long id;
    private CardType cardType;
    private String number;
    private short cvv;
    private LocalDate fromDate;
    private LocalDate thruDate;
    private String cardHolder;
    private CardColor cardColor;

    public CardDTO(Card card) {
        this.id = card.getId();
        this.cardType = card.getCardType();
        this.cardColor = card.getCardColor();
        this.number = card.getNumber();
        this.fromDate = card.getFromDate();
        this.thruDate = card.getThruDate();
        this.cvv = card.getCvv();
        this.cardHolder = card.getCardHolder();
    }

    public long getId() {
        return id;
    }

    public CardType getCardType() {
        return cardType;
    }

    public String getNumber() {
        return number;
    }

    public short getCvv() {
        return cvv;
    }

    public LocalDate getFromDate() {
        return fromDate;
    }

    public LocalDate getThruDate() {
        return thruDate;
    }

    public String getCardHolder() {
        return cardHolder;
    }

    public CardColor getCardColor() {
        return cardColor;
    }
}
