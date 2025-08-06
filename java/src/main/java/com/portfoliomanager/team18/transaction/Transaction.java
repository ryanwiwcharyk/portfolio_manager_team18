package com.portfoliomanager.team18.transaction;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.*;

@Entity
@Table(name = "transactions")
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer transactionID;

    private String tickerSymbol;

    private Integer portfolioID;

    private Double price;

    private Integer qty;

    private Boolean isSell;

    private ZonedDateTime transactionTime;

    public Transaction() {
    }

    public Transaction(Integer transactionID, String tickerSymbol, Integer portfolioID, Double price, Integer qty, Boolean isSell, ZonedDateTime transactionTime) {
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

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
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

    public ZonedDateTime getTransactionTime() {
        return transactionTime;
    }

    public void setTransactionTime(ZonedDateTime transactionTime) {
        this.transactionTime = transactionTime;
    }
}