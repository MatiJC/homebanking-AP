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

    private CardType type;
    private String number;
    private short cvv;
    private LocalDate fromDate;
    private LocalDate thruDate;

    private String cardHolder;

    private CardColor color;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "owner_id")
    private Client client;

    public Card(){

    }

    public Card(CardType type, CardColor color, String number, LocalDate fromDate, LocalDate thruDate, short cvv, String cardHolder
                ) {
        this.type = type;
        this.color = color;
        this.number = number;
        this.fromDate = fromDate;
        this.thruDate = thruDate;
        this.cvv = cvv;
        this.cardHolder = cardHolder;
    }


    public long getId() {
        return id;
    }

    public CardType getType() {
        return type;
    }

    public void setType(CardType cardType) {
        this.type = cardType;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public short getCvv() {
        return cvv;
    }

    public void setCvv(short securityCode) {
        this.cvv = securityCode;
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

    public String getCardHolder() {
        return cardHolder;
    }

    public void setCardHolder(String cardOwner) {
        this.cardHolder = cardOwner;
    }

    public CardColor getColor() {
        return color;
    }

    public void setColor(CardColor cardColor) {
        this.color = cardColor;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }
}
