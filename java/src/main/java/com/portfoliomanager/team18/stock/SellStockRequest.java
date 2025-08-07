package com.portfoliomanager.team18.stock;

public class SellStockRequest {
    private Integer portfolioID;
    private String tickerSymbol;
    private Integer qty;

    public SellStockRequest(Integer portfolioID, String tickerSymbol, Integer qty) {
        this.portfolioID = portfolioID;
        this.tickerSymbol = tickerSymbol;
        this.qty = qty;
    }

    public Integer getPortfolioID() {
        return portfolioID;
    }

    public void setPortfolioID(Integer portfolioID) {
        this.portfolioID = portfolioID;
    }

    public String getTickerSymbol() {
        return tickerSymbol;
    }

    public void setTickerSymbol(String tickerSymbol) {
        this.tickerSymbol = tickerSymbol;
    }

    public Integer getQty() {
        return qty;
    }

    public void setQty(Integer qty) {
        this.qty = qty;
    }

    @Override
    public String toString() {
        return "SellStockRequest{" +
                "portfolioID=" + portfolioID +
                ", tickerSymbol='" + tickerSymbol + '\'' +
                ", numberOfShares=" + qty +
                '}';
    }
}
