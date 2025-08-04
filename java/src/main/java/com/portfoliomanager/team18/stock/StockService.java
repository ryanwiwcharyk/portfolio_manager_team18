package com.portfoliomanager.team18.stock;

import com.portfoliomanager.team18.portfolio.Portfolio;
import com.portfoliomanager.team18.portfolio.PortfolioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class StockService {
    @Autowired
    private StockRepository stockRepo;
    @Autowired
    private PortfolioRepository portfolioRepo;

    public List<Stock> getStockByPortfolioId(Integer id) {
        return stockRepo.findByPortfolioID(id);
    }

    public Stock getStockByTickerAndPortfolioId(String ticker, Integer portfolioID) {
        Optional<Stock> existingStock = stockRepo.findById(new StockId(ticker, portfolioID));
        if (existingStock.isEmpty()) {
            throw new IllegalArgumentException("Stock " + ticker + " does not exist in portfolio " + portfolioID + ".");
        }

        return existingStock.get();
    }

    public Stock saveNewStockRequest(NewStockRequest req) {
        // Check if portfolio exists
        Optional<Portfolio> existingPortfolio = portfolioRepo.findById(req.getPortfolioID());
        if (existingPortfolio.isEmpty()) {
            throw new IllegalArgumentException("Portfolio with ID " + req.getPortfolioID() + " does not exist.");
        }

        StockId stockId = new StockId(req.getTickerSymbol(), req.getPortfolioID());

        // Check if stock already exists
        Optional<Stock> existingStock = stockRepo.findById(stockId);
        if (existingStock.isPresent()) {
            throw new IllegalArgumentException("Stock " + req.getTickerSymbol() + " already exists in portfolio " +
                    req.getPortfolioID() + ". Please use the update method.");
        }

        Stock stock = new Stock();
        stock.setTickerSymbol(req.getTickerSymbol());
        stock.setPortfolioID(req.getPortfolioID());
        stock.setQty(req.getQty());
        stock.setAvgPrice(req.getAvgPrice());

        return stockRepo.save(stock);
    }

    public Stock updateStockRequest(String ticker, Integer portfolioID, UpdateStockRequest req) {
        Optional<Stock> existingStock = stockRepo.findById(new StockId(ticker, portfolioID));
        if (existingStock.isEmpty()) {
            throw new IllegalArgumentException("Stock " + ticker + " does not exist in portfolio " + portfolioID +
                    ". Please add the stock first.");
        }

        Stock stock = existingStock.get();
        int newTotalQty = stock.getQty() + req.getQty();

        BigDecimal newAvgPrice = calculateAveragePrice(
                stock.getAvgPrice(), stock.getQty(),
                req.getAvgPrice(), req.getQty()
        );

        stock.setQty(newTotalQty);
        stock.setAvgPrice(newAvgPrice);

        return stockRepo.save(stock);
    }

    public void deleteStockById(String ticker, Integer portfolioID) {
        Optional<Stock> existingStock = stockRepo.findById(new StockId(ticker, portfolioID));
        if (existingStock.isEmpty()) {
            throw new IllegalArgumentException("Stock " + ticker + " does not exist in portfolio " + portfolioID + ".");
        }

        stockRepo.deleteById(new StockId(ticker, portfolioID));
    }

    private BigDecimal calculateAveragePrice(BigDecimal currentAvgPrice, int currentQty, BigDecimal newPrice, int newQty) {
        BigDecimal totalCost = currentAvgPrice.multiply(BigDecimal.valueOf(currentQty))
                .add(newPrice.multiply(BigDecimal.valueOf(newQty)));

        int totalQty = currentQty + newQty;

        if (totalQty == 0) {
            return BigDecimal.ZERO;
        }

        return totalCost.divide(BigDecimal.valueOf(totalQty));
    }
}
