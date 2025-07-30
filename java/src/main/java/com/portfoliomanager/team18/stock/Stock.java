package com.portfoliomanager.team18.stock;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "stocks")
@IdClass(StockId.class)
public class Stock {
    @Id
    private String tickerSymbol;

    @Id
    private Integer portfolioID;

    private Integer qty;

    private BigDecimal avgPrice;

    public Stock() {
    }

    public Stock(String tickerSymbol, Integer portfolioID, Integer qty, BigDecimal avgPrice) {
        this.tickerSymbol = tickerSymbol;
        this.portfolioID = portfolioID;
        this.qty = qty;
        this.avgPrice = avgPrice;
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

    public Integer getQty() {
        return qty;
    }

    public void setQty(Integer qty) {
        this.qty = qty;
    }

    public BigDecimal getAvgPrice() {
        return avgPrice;
    }

    public void setAvgPrice(BigDecimal avgPrice) {
        this.avgPrice = avgPrice;
    }
}
