package com.portfoliomanager.team18.stock;

import com.crazzyghost.alphavantage.AlphaVantage;
import com.crazzyghost.alphavantage.timeseries.response.QuoteResponse;
import com.portfoliomanager.team18.exception.InsufficientCashException;
import com.portfoliomanager.team18.portfolio.Portfolio;
import com.portfoliomanager.team18.portfolio.PortfolioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class StockService {
    private final Logger logger = LoggerFactory.getLogger(StockService.class);
    @Autowired
    private StockRepository stockRepo;
    @Autowired
    private PortfolioRepository portfolioRepo;

    public List<Stock> getStockByPortfolioId(Integer id) {
        List<Stock> stocks = stockRepo.findByPortfolioID(id);
        for (Stock stock : stocks) {
            String ticker = stock.getTickerSymbol();
            QuoteResponse quote = AlphaVantage
                    .api()
                    .timeSeries()
                    .quote()
                    .forSymbol(ticker)
                    .fetchSync();
            if (!Objects.equals(stock.getCurrentPrice(), BigDecimal.valueOf(quote.getPrice()))) {
                stock.setCurrentPrice(quote.getPrice());
            }
        }
        stockRepo.saveAll(stocks);
        return stocks;
    }

    public Stock getStockByTickerAndPortfolioId(String ticker, Integer portfolioID) {
        Optional<Stock> existingStock = stockRepo.findById(new StockId(ticker, portfolioID));
        if (existingStock.isEmpty()) {
            throw new IllegalArgumentException("Stock " + ticker + " does not exist in portfolio " + portfolioID + ".");
        }

        return existingStock.get();
    }

    public NewStockDTO saveNewStockRequest(NewStockRequest req) {
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

        QuoteResponse quote =
                AlphaVantage
                        .api()
                        .timeSeries()
                        .quote()
                        .forSymbol(req.getTickerSymbol())
                        .fetchSync();
        logger.info("Received stock quote from AlphaVantage API:" +
                "Symbol: {}" +
                "Price: {}," +
                "Change %: {}", quote.getSymbol(), quote.getPrice(), quote.getChangePercent());
        //check if portfolio has enough cash to purchase
        logger.debug("User wants {} x{}", req.getTickerSymbol(), req.getQty());
        logger.debug("Portfolio cash: {}\nCash to purchase: {}", existingPortfolio.get().getCash(), existingPortfolio.get().getCash() - (quote.getPrice() * req.getQty()));
        if (existingPortfolio.get().getCash() - (quote.getPrice() * req.getQty()) < 0)
            throw new InsufficientCashException("Your portfolio does not have enough cash to purchase x" + req.getQty()
                    + " of " + req.getTickerSymbol());

        existingPortfolio.get().setCash(existingPortfolio.get().getCash() - quote.getPrice() * req.getQty());

        Stock stock = new Stock();
        stock.setTickerSymbol(req.getTickerSymbol());
        stock.setPortfolioID(req.getPortfolioID());
        stock.setQty(req.getQty());
        stock.setCurrentPrice(quote.getPrice());
        stock.setAvgPrice(0);
        stock.setChangePercent(quote.getChangePercent());
        logger.debug("Saving stock: {}", stock);
        stockRepo.save(stock);

        NewStockDTO stockDTO = new NewStockDTO(
                stock.getTickerSymbol(),
                stock.getPortfolioID(),
                stock.getQty(),
                stock.getCurrentPrice(),
                stock.getAvgPrice(),
                stock.getChangePercent(),
                existingPortfolio.get().getCash()
        );
        logger.debug("Returning stock dto to client: {}", stockDTO);
        return stockDTO;

    }

    public Stock updateStockRequest(String ticker, Integer portfolioID, UpdateStockRequest req) {
        Optional<Stock> existingStock = stockRepo.findById(new StockId(ticker, portfolioID));
        if (existingStock.isEmpty()) {
            throw new IllegalArgumentException("Stock " + ticker + " does not exist in portfolio " + portfolioID +
                    ". Please add the stock first.");
        }

        Stock stock = existingStock.get();
        int newTotalQty = stock.getQty() + req.getQty();

//        double newAvgPrice = calculateAveragePrice(
//                BigDecimal.valueOf(stock.getAvgPrice()), stock.getQty(),
//                req.getAvgPrice(), req.getQty()
//        );
//
//        stock.setQty(newTotalQty);
//        stock.setAvgPrice(newAvgPrice);

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
