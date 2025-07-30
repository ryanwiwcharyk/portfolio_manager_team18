package com.portfoliomanager.team18.stock;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StockRepository extends JpaRepository<Stock, StockId> {
    List<Stock> findByPortfolioID(Integer portfolioID);
}
