package com.portfoliomanager.team18.portfolio;

import java.math.BigDecimal;

public class UpdatePortfolioRequest {
    private String portfolioName;
    private BigDecimal cash;

    public UpdatePortfolioRequest(String portfolioName, BigDecimal cash) {
        this.portfolioName = portfolioName;
        this.cash = cash;
    }

    public String getPortfolioName() {
        return portfolioName;
    }

    public void setPortfolioName(String portfolioName) {
        this.portfolioName = portfolioName;
    }

    public BigDecimal getCash() {
        return cash;
    }

    public void setCash(BigDecimal cash) {
        this.cash = cash;
    }

    @Override
    public String toString() {
        return "UpdatePortfolioRequest{" +
                "portfolioName='" + portfolioName + '\'' +
                ", cash=" + cash +
                '}';
    }
}
