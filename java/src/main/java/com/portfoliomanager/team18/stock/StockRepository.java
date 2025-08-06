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
    @Transactional
    @Query("UPDATE Stock s SET s.qty = :quantity WHERE s.portfolioID = :portfolioId AND s.tickerSymbol = :tickerSymbol")
    void sellPartialStock(@Param("portfolioId") Integer portfolioId,
                         @Param("tickerSymbol") String tickerSymbol,
                         @Param("quantity") Integer quantity);
//    @Modifying
//    @Transactional
//    @Query("DELETE FROM stocks s WHERE s.portfolioid = :portfolioId AND s.ticker_symbol = :tickerSymbol")
//    void sellAllStock(@Param("portfolioId") Long portfolioId,
//                      @Param("tickerSymbol") String tickerSymbol);
}
