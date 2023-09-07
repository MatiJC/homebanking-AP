package com.mindhub.homebanking.dto;

public class LoanApplicationDTO {
    private Long id;
    private double amount;
    private int payments;
    private String toAccountNumber;

    public LoanApplicationDTO() {
    }

    public LoanApplicationDTO(long id, double amount, int payments, String toAccountNumber) {
        this.id = id;
        this.amount = amount;
        this.payments = payments;
        this.toAccountNumber = toAccountNumber;
    }

    public Long getId() {
        return id;
    }

    public double getAmount() {
        return amount;
    }

    public int getPayments() {
        return payments;
    }

    public String getToAccountNumber() {
        return toAccountNumber;
    }
}
