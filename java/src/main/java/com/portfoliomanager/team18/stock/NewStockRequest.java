package com.portfoliomanager.team18.stock;

import java.math.BigDecimal;

public class NewStockRequest {
    private String tickerSymbol;
    private Integer portfolioID;
    private Integer qty;

    public NewStockRequest(String tickerSymbol, Integer portfolioID, Integer qty) {
        this.tickerSymbol = tickerSymbol;
        this.portfolioID = portfolioID;
        this.qty = qty;
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

    @Override
    public String toString() {
        return "NewStockRequest{" +
                "tickerSymbol='" + tickerSymbol + '\'' +
                ", portfolioID=" + portfolioID +
                ", qty=" + qty +
                '}';
    }
}
