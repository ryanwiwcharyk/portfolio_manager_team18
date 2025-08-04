package com.portfoliomanager.team18.portfolio;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "portfolios")
public class Portfolio {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer portfolioID;
    private String portfolioName;
    private String description;
    private BigDecimal cash;

    public Portfolio() {
    }

    public Portfolio(Integer portfolioID, String portfolioName, String description, BigDecimal cash) {
        this.portfolioID = portfolioID;
        this.portfolioName = portfolioName;
        this.description = description;
        this.cash = cash;
    }

    public Integer getPortfolioID() {
        return portfolioID;
    }

    public void setPortfolioID(Integer portfolioID) {
        this.portfolioID = portfolioID;
    }

    public String getPortfolioName() {
        return portfolioName;
    }

    public void setPortfolioName(String portfolioName) {
        this.portfolioName = portfolioName;
    }

    public String getDescription() { return description;}

    public void setDescription(String description) { this.description = description; }

    public BigDecimal getCash() {
        return cash;
    }

    public void setCash(BigDecimal cash) {
        this.cash = cash;
    }
}
