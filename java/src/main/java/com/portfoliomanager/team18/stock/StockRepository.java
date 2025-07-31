package com.portfoliomanager.team18.stock;

import com.portfoliomanager.team18.portfolio.Portfolio;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StockRepository extends JpaRepository<Stock, StockId> {
    List<Stock> findByPortfolioID(Integer portfolioID);

    Optional<Portfolio> findByTickerSymbol(String portfolioName);
}
