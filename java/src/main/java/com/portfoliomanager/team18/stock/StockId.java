package com.portfoliomanager.team18.stock;

import java.io.Serializable;
import java.util.Objects;

public class StockId implements Serializable {
    private String tickerSymbol;
    private Integer portfolioID;

    public StockId(String tickerSymbol, Integer portfolioID) {
        this.tickerSymbol = tickerSymbol;
        this.portfolioID = portfolioID;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof StockId)) return false;
        StockId stockId = (StockId) o;
        return tickerSymbol.equals(stockId.tickerSymbol) && portfolioID.equals(stockId.portfolioID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tickerSymbol, portfolioID);
    }

}
