package com.portfoliomanager.team18.transaction;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Entity
@Table(name = "transactions")
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer transactionID;

    private String tickerSymbol;

    private Integer portfolioID;

    private BigDecimal price;

    private Integer qty;

    private Boolean isSell;

    private Timestamp transactionTime;

    public Transaction() {
    }

    public Transaction(Integer transactionID, String tickerSymbol, Integer portfolioID, BigDecimal price, Integer qty, Boolean isSell, Timestamp transactionTime) {
        this.transactionID = transactionID;
        this.tickerSymbol = tickerSymbol;
        this.portfolioID = portfolioID;
        this.price = price;
        this.qty = qty;
        this.isSell = isSell;
        this.transactionTime = transactionTime;
    }

    public Integer getTransactionID() {
        return transactionID;
    }

    public void setTransactionID(Integer transactionID) {
        this.transactionID = transactionID;
    }

    public String getTickerSymbol() {
        return tickerSymbol;
    }

    public void setTickerSymbol(String tickerSymbol) {
        this.tickerSymbol = tickerSymbol;
    }

    public Integer getPortfolioID() {
        return portfolioID;
    }

    public void setPortfolioID(Integer portfolioID) {
        this.portfolioID = portfolioID;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getQty() {
        return qty;
    }

    public void setQty(Integer qty) {
        this.qty = qty;
    }

    public Boolean getSell() {
        return isSell;
    }

    public void setSell(Boolean sell) {
        isSell = sell;
    }

    public Timestamp getTransactionTime() {
        return transactionTime;
    }

    public void setTransactionTime(Timestamp transactionTime) {
        this.transactionTime = transactionTime;
    }
}