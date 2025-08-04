package com.portfoliomanager.team18.api.response;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class YahooFinanceAPIResponse {

    private String symbol;
    private BigDecimal price;
    private BigDecimal change;
    public YahooFinanceAPIResponse(String symbol, BigDecimal price, BigDecimal change) {
        this.symbol = symbol;
        this.price = price;
        this.change = change;
    }
}
