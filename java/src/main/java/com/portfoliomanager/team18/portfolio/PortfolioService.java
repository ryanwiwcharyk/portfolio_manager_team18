package com.portfoliomanager.team18.portfolio;

import com.portfoliomanager.team18.exception.PortfolioIllegalArgumentException;
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
        Optional<Portfolio> existingPortfolio = portfolioRepo.findById(id);
        if (existingPortfolio.isEmpty()) {
            throw new PortfolioIllegalArgumentException("Portfolio doesn't exist.");
        }

        return existingPortfolio.get();
    }

    public Portfolio saveNewPortfolioRequest(NewPortfolioRequest req) {
        Optional<Portfolio> existing = portfolioRepo.findByPortfolioName(req.getPortfolioName());
        if (existing.isPresent()) {
            throw new PortfolioIllegalArgumentException("Portfolio with name '" + req.getPortfolioName() + "' already exists.");
        }

        Portfolio p = new Portfolio();
        p.setPortfolioName(req.getPortfolioName());
        p.setDescription(req.getDescription());
        p.setCash(req.getCash());
        return portfolioRepo.save(p);
    }

    public Portfolio updatePortfolioRequest(Integer id, UpdatePortfolioRequest req) {
        Optional<Portfolio> existingPortfolio = portfolioRepo.findById(id);
        if (existingPortfolio.isEmpty()) {
            throw new PortfolioIllegalArgumentException("Portfolio with name '" + req.getPortfolioName() + "' doesn't exist.");
        }

        Portfolio p = existingPortfolio.get();
        p.setPortfolioName(req.getPortfolioName());
        p.setDescription(req.getDescription());
        p.setCash(req.getCash());

        return portfolioRepo.save(p);
    }

    public void deletePortfolioById(Integer id) {
        Optional<Portfolio> existingPortfolio = portfolioRepo.findById(id);
        if (existingPortfolio.isEmpty()) {
            throw new PortfolioIllegalArgumentException("Portfolio doesn't exist.");
        }

        portfolioRepo.deleteById(id);
    }
}
