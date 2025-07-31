package com.portfoliomanager.team18.portfolio;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PortfolioRepository extends JpaRepository<Portfolio, Integer> {
    Optional<Portfolio> findByPortfolioName(String portfolioName);
    Portfolio findByPortfolioID(Integer portfolioId);
}

