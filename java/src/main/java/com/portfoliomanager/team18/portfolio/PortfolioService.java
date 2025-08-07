package com.portfoliomanager.team18.portfolio;

import com.portfoliomanager.team18.transaction.Transaction;
import com.portfoliomanager.team18.transaction.TransactionRepository;
import com.portfoliomanager.team18.stock.StockRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Service
public class PortfolioService {
    @Autowired
    private PortfolioRepository portfolioRepo;

    @Autowired
    private TransactionRepository transactionRepo;
    @Autowired
    private StockRepository stockRepository;

    public List<Portfolio> getAllPortfolio() {
        return portfolioRepo.findAll();
    }

    public Portfolio getPortfolioById(Integer id) {
        Optional<Portfolio> existingPortfolio = portfolioRepo.findById(id);
        if (existingPortfolio.isEmpty()) {
            throw new IllegalArgumentException("Portfolio doesn't exist.");
        }

        return existingPortfolio.get();
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
        Optional<Portfolio> existingPortfolio = portfolioRepo.findById(id);
        if (existingPortfolio.isEmpty()) {
            throw new IllegalArgumentException("Portfolio with name '" + req.getPortfolioName() + "' doesn't exist.");
        }

        Portfolio p = existingPortfolio.get();
        p.setPortfolioName(req.getPortfolioName());
        p.setDescription(req.getDescription());
        p.setCash(req.getCash());

        return portfolioRepo.save(p);
    }

    public Portfolio updateCash(Integer id, Double cash) {
        Optional<Portfolio> existingPortfolio = portfolioRepo.findById(id);
        if (existingPortfolio.isEmpty()) {
            throw new IllegalArgumentException("Portfolio doesn't exist.");
        }

        Portfolio p = existingPortfolio.get();
        if (p.getCash() < Math.abs(cash) && cash < 0){
            throw new IllegalArgumentException("Cannot withdraw more than cash value");
        }
        p.setCash(p.getCash() + cash);
        Portfolio updated = portfolioRepo.save(p);

        // Record the cash transaction
        Transaction cashTransaction = new Transaction();
        cashTransaction.setTickerSymbol(cash < 0 ? "WITHDRAW" : "DEPOSIT");
        cashTransaction.setPortfolioID(id);
        cashTransaction.setPrice(BigDecimal.valueOf(Math.abs(cash)));
        cashTransaction.setQty(1);
        cashTransaction.setSell(cash < 0); // true if withdraw, false if deposit
        cashTransaction.setTransactionTime(new Timestamp(System.currentTimeMillis()));
        transactionRepo.save(cashTransaction);

        return updated;
    }

    public void deletePortfolioById(Integer id) {
        Optional<Portfolio> existingPortfolio = portfolioRepo.findById(id);
        if (existingPortfolio.isEmpty()) {
            throw new IllegalArgumentException("Portfolio doesn't exist.");
        }

        portfolioRepo.deleteById(id);
        stockRepository.deleteAllByPortfolioID(id);
    }
}
