package com.portfoliomanager.team18.stock;

import com.portfoliomanager.team18.portfolio.Portfolio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface StockRepository extends JpaRepository<Stock, StockId> {
    List<Stock> findByPortfolioID(Integer portfolioID);
    Optional<Portfolio> findByTickerSymbol(String portfolioName);
    @Modifying
    @Query("UPDATE Stock s SET s.currentPrice = (SELECT sd.price FROM StockData sd WHERE sd.tickerSymbol = s.tickerSymbol) WHERE s.portfolioID = :portfolioId AND s.currentPrice != (SELECT sd.price FROM StockData sd WHERE sd.tickerSymbol = s.tickerSymbol)")
    int updateOutdatedStockPrices(@Param("portfolioId") Integer portfolioId);
    @Modifying
    @Transactional
    @Query("DELETE FROM Stock s WHERE s.portfolioID = :portfolioID")
    void deleteAllByPortfolioID(Integer portfolioID);
}
