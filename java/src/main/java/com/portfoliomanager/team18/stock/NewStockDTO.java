package com.portfoliomanager.team18.stock;

public class NewStockDTO {
    private String tickerSymbol;
    private Integer portfolioID;
    private Integer qty;
    private double currentPrice;
    private double avgPrice;
    private double changePercent;
    private double updatedCash;

    public NewStockDTO() {}

    public NewStockDTO(String tickerSymbol, Integer portfolioID, Integer qty, double currentPrice, double avgPrice, double changePercent, double updatedCash) {
        this.tickerSymbol = tickerSymbol;
        this.portfolioID = portfolioID;
        this.qty = qty;
        this.currentPrice = currentPrice;
        this.avgPrice = avgPrice;
        this.changePercent = changePercent;
        this.updatedCash = updatedCash;
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

    public double getCurrentPrice() {
        return currentPrice;
    }

    public void setCurrentPrice(double currentPrice) {
        this.currentPrice = currentPrice;
    }

    public double getAvgPrice() {
        return avgPrice;
    }

    public void setAvgPrice(double avgPrice) {
        this.avgPrice = avgPrice;
    }

    public double getChangePercent() {
        return changePercent;
    }

    public void setChangePercent(double changePercent) {
        this.changePercent = changePercent;
    }

    public double getUpdatedCash() {
        return updatedCash;
    }

    public void setUpdatedCash(double updatedCash) {
        this.updatedCash = updatedCash;
    }
}
