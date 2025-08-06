package com.portfoliomanager.team18.stock;

import jakarta.persistence.*;

@Entity
@Table(name = "stock_data")  // Make sure the name matches the table in your DB
public class StockData {

    @Id
    @Column(name = "id")
    private Integer id;  // Assuming the table has a primary key column 'id'

    @Column(name = "ticker_symbol")
    private String tickerSymbol;

    @Column(name = "price")
    private Double price;

    @Column(name = "change_percent")
    private Double changePercent;

    // Constructors, Getters and Setters
    public StockData() {}

    public StockData(Integer id, String symbol, Double price, Double changePercent) {
        this.id = id;
        this.tickerSymbol = symbol;
        this.price = price;
        this.changePercent = changePercent;
    }

    // Getters and Setters

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTickerSymbol() {
        return tickerSymbol;
    }

    public void setTickerSymbol(String tickerSymbol) {
        this.tickerSymbol = tickerSymbol;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Double getChangePercent() {
        return changePercent;
    }

    public void setChangePercent(Double changePercent) {
        this.changePercent = changePercent;
    }
}