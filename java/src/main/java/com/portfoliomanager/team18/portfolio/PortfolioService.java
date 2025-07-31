package com.portfoliomanager.team18.portfolio;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PortfolioService {
    @Autowired
    private PortfolioRepository portfolioRepo;

    public Portfolio getByPortfolioId(Integer id) { return portfolioRepo.findByPortfolioID(id); }

    public List<Portfolio> getAll() {
        return portfolioRepo.findAll();
    }

    public Portfolio save(Portfolio portfolio) {
        return portfolioRepo.save(portfolio);
    }
}
