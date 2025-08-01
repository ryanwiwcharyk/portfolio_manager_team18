package com.portfoliomanager.team18.portfolio;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PortfolioService {
    @Autowired
    private PortfolioRepository portfolioRepo;

    public List<Portfolio> getAllPortfolio() {
        return portfolioRepo.findAll();
    }

    public Portfolio getPortfolioById(Integer id) {
        return portfolioRepo.findById(id).orElse(null);
    }

    public Portfolio saveNewPortfolioRequest(NewPortfolioRequest req) {
        Optional<Portfolio> existing = portfolioRepo.findByPortfolioName(req.getPortfolioName());
        if (existing.isPresent()) {
            throw new IllegalArgumentException("Portfolio with name '" + req.getPortfolioName() + "' already exists.");
        }

        Portfolio p = new Portfolio();
        p.setPortfolioName(req.getPortfolioName());
        p.setDescription(req.getDescription());
        p.setCash(req.getCash());
        return portfolioRepo.save(p);
    }

    public Portfolio updatePortfolioRequest(Integer id, UpdatePortfolioRequest req) {
        Portfolio p = portfolioRepo.findById(id).orElse(null);
        if (p != null) {
            p.setPortfolioName(req.getPortfolioName());
            p.setDescription(req.getDescription());
            p.setCash(req.getCash());
            return portfolioRepo.save(p);
        }
        return null;
    }

    public void deletePortfolioById(Integer id) {
        portfolioRepo.deleteById(id);
    }
}
