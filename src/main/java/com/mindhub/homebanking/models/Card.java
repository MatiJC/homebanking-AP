package com.mindhub.homebanking.models;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
public class Card {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;

    private CardType cardType;
    private String number;
    private short securityCode;
    private LocalDate fromDate;
    private LocalDate thruDate;

    private String cardOwner;

    private CardColor cardColor;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "owner_id")
    private Client client;

    public Card(){

    }

    public Card(CardType type, String number, short code, LocalDate fromDate, LocalDate thruDate, String owner,
                CardColor cardColor, Client client) {
        this.cardType = type;
        this.number = number;
        this.securityCode = code;
        this.fromDate = fromDate;
        this.thruDate = thruDate;
        this.cardOwner = owner;
        this.cardColor = cardColor;
        this.client = client;
    }

    public long getId() {
        return id;
    }

    public CardType getCardType() {
        return cardType;
    }

    public void setCardType(CardType cardType) {
        this.cardType = cardType;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public short getSecurityCode() {
        return securityCode;
    }

    public void setSecurityCode(short securityCode) {
        this.securityCode = securityCode;
    }

    public LocalDate getFromDate() {
        return fromDate;
    }

    public void setFromDate(LocalDate fromDate) {
        this.fromDate = fromDate;
    }

    public LocalDate getThruDate() {
        return thruDate;
    }

    public void setThruDate(LocalDate thruDate) {
        this.thruDate = thruDate;
    }

    public String getCardOwner() {
        return cardOwner;
    }

    public void setCardOwner(String cardOwner) {
        this.cardOwner = cardOwner;
    }

    public CardColor getCardColor() {
        return cardColor;
    }

    public void setCardColor(CardColor cardColor) {
        this.cardColor = cardColor;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }
}
