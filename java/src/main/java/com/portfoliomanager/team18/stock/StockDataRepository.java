package com.portfoliomanager.team18.stock;

import org.springframework.data.jpa.repository.JpaRepository;

public interface StockDataRepository extends JpaRepository<StockData, Integer> {
    StockData findByTickerSymbol(String tickerSymbol);
}
