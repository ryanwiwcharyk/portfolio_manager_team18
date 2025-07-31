package com.portfoliomanager.team18.api.client;


import com.portfoliomanager.team18.api.response.YahooFinanceAPIResponse;
import org.springframework.stereotype.Component;
import yahoofinance.Stock;
import yahoofinance.YahooFinance;

import java.io.IOException;

@Component
public class YahooFinanceClient {

    public YahooFinanceAPIResponse fetchStockData(String symbol) {
        try {
            Stock stock = YahooFinance.get(symbol);
            return new YahooFinanceAPIResponse(
                    stock.getSymbol(),
                    stock.getQuote().getPrice(),
                    stock.getQuote().getChange()
            );
        } catch (IOException e) {
            throw new RuntimeException(String.format("There was an issue fetching stock data for %s", symbol), e);
        }
    }
}
