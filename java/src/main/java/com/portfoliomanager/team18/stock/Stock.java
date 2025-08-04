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
    private BigDecimal currentPrice;
    private BigDecimal avgPrice;
    private BigDecimal changePercent;

    public Stock() {}

    public Stock(String tickerSymbol, Integer portfolioID, Integer qty, BigDecimal currentPrice, BigDecimal avgPrice, BigDecimal changePercent) {
        this.tickerSymbol = tickerSymbol;
        this.portfolioID = portfolioID;
        this.qty = qty;
        this.currentPrice = currentPrice;
        this.avgPrice = avgPrice;
        this.changePercent = changePercent;
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

    public BigDecimal getCurrentPrice() {
        return currentPrice;
    }

    public void setCurrentPrice(BigDecimal currentPrice) {
        this.currentPrice = currentPrice;
    }

    public BigDecimal getAvgPrice() {
        return avgPrice;
    }

    public void setAvgPrice(BigDecimal avgPrice) {
        this.avgPrice = avgPrice;
    }

    public BigDecimal getChangePercent() {
        return changePercent;
    }

    public void setChangePercent(BigDecimal changePercent) {
        this.changePercent = changePercent;
    }
}
