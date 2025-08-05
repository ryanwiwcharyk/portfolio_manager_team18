package com.portfoliomanager.team18.portfolio;

import java.math.BigDecimal;

public class UpdatePortfolioRequest {
    private String portfolioName;
    private String description;
    private double cash;

    public UpdatePortfolioRequest(String portfolioName, String description, double cash) {
        this.portfolioName = portfolioName;
        this.description = description;
        this.cash = cash;
    }

    public String getPortfolioName() {
        return portfolioName;
    }

    public void setPortfolioName(String portfolioName) {
        this.portfolioName = portfolioName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getCash() {
        return cash;
    }

    public void setCash(double cash) {
        this.cash = cash;
    }

    @Override
    public String toString() {
        return "UpdatePortfolioRequest{" +
                "portfolioName='" + portfolioName + '\'' +
                ", description='" + description + '\'' +
                ", cash=" + cash +
                '}';
    }
}
