package com.mindhub.homebanking.dto;

import com.mindhub.homebanking.models.ClientLoan;

public class ClientLoanDTO {

    private long id_clientLoan;
    private long id_loan;
    private String loanName;
    private double amountRequested;
    private int paymentsToMake;

    public ClientLoanDTO() {
    }

    public ClientLoanDTO(ClientLoan clientLoan) {
        this.id_clientLoan = clientLoan.getId();
        this.id_loan = clientLoan.getLoan().getId();
        this.loanName = clientLoan.getLoan().getName();
        this.amountRequested = clientLoan.getAmount();
        this.paymentsToMake = clientLoan.getPayments();
    }


    public long getId_clientLoan() {
        return id_clientLoan;
    }

    public long getId_loan() {
        return id_loan;
    }

    public String getLoanName() {
        return loanName;
    }

    public double getAmountRequested() {
        return amountRequested;
    }

    public int getPaymentsToMake() {
        return paymentsToMake;
    }


}
