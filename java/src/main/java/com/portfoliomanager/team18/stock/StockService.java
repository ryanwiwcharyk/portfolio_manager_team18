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
        return stockRepo.findById(new StockId(ticker, portfolioID)).orElse(null);
    }

    /*
    Three situations:
    1. portfolioId doesn't exists, return exception
    2. portfolioId and ticker both exist, update stock quantity and average price
    3. portfolioId exists, ticker doesn't exist, add this stock to portfolio
     */
    public Stock saveNewStockRequest(NewStockRequest req) {
        // Case 1. Check if portfolio exists
        Optional<Portfolio> portfolioOpt = portfolioRepo.findById(req.getPortfolioID());
        if (portfolioOpt.isEmpty()) {
            throw new IllegalArgumentException("Portfolio with ID " + req.getPortfolioID() + " does not exist.");
        }
        // Construct composite key
        StockId stockId = new StockId(req.getTickerSymbol(), req.getPortfolioID());

        // Check if stock already exists
        Optional<Stock> existingStock = stockRepo.findById(stockId);

        Stock stock;
        if (existingStock.isPresent()) {
            // Case 2: Update stock quantity and calculate new average price
            stock = existingStock.get();
            int newTotalQty = stock.getQty() + req.getQty();

            BigDecimal newAvgPrice = calculateAveragePrice(
                    stock.getAvgPrice(), stock.getQty(),
                    req.getAvgPrice(), req.getQty()
            );

            stock.setQty(newTotalQty);
            stock.setAvgPrice(newAvgPrice);
        } else {
            // Case 3: Add new stock
            stock = new Stock();
            stock.setTickerSymbol(req.getTickerSymbol());
            stock.setPortfolioID(req.getPortfolioID());
            stock.setQty(req.getQty());
            stock.setAvgPrice(req.getAvgPrice());
        }
        return stockRepo.save(stock);
    }

    public Stock updateStockRequest(String ticker, Integer portfolioID, UpdateStockRequest req) {
        Stock stock = stockRepo.findById(new StockId(ticker, portfolioID)).orElse(null);
        if (stock != null) {
            int newTotalQty = stock.getQty() + req.getQty();

            BigDecimal newAvgPrice = calculateAveragePrice(
                    stock.getAvgPrice(), stock.getQty(),
                    req.getAvgPrice(), req.getQty()
            );

            stock.setQty(newTotalQty);
            stock.setAvgPrice(newAvgPrice);
            return stockRepo.save(stock);
        }
        return null;
    }

    public void deleteStockById(String ticker, Integer portfolioID) {
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
