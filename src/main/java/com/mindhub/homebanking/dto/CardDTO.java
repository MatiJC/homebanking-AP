package com.mindhub.homebanking.dto;

import com.mindhub.homebanking.models.Card;
import com.mindhub.homebanking.models.CardColor;
import com.mindhub.homebanking.models.CardType;

import java.time.LocalDate;

public class CardDTO {
    private long id;
    private CardType cardType;
    private String number;
    private short securityCode;
    private LocalDate fromDate;
    private LocalDate thruDate;
    private String cardOwner;
    private CardColor cardColor;

    public CardDTO(Card card) {
        this.id = card.getId();
        this.cardType = card.getCardType();
        this.number = card.getNumber();
        this.securityCode = card.getSecurityCode();
        this.fromDate = card.getFromDate();
        this.thruDate = card.getThruDate();
        this.cardOwner = card.getCardOwner();
        this.cardColor = card.getCardColor();
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

    public short getSecurityCode() {
        return securityCode;
    }

    public LocalDate getFromDate() {
        return fromDate;
    }

    public LocalDate getThruDate() {
        return thruDate;
    }

    public String getCardOwner() {
        return cardOwner;
    }

    public CardColor getCardColor() {
        return cardColor;
    }
}
