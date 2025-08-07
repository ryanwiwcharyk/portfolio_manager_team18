package com.portfoliomanager.team18.stock;

import com.crazzyghost.alphavantage.AlphaVantage;
import com.crazzyghost.alphavantage.timeseries.response.QuoteResponse;
import com.portfoliomanager.team18.exception.InsufficientCashException;
import com.portfoliomanager.team18.exception.StockIllegalArgumentException;
import com.portfoliomanager.team18.portfolio.Portfolio;
import com.portfoliomanager.team18.portfolio.PortfolioRepository;
import com.portfoliomanager.team18.transaction.Transaction;
import com.portfoliomanager.team18.transaction.TransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.ZoneId;
import java.time.ZonedDateTime;
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
    @Autowired
    private StockDataRepository stockDataRepository;
    @Autowired
    private TransactionRepository transactionRepository;

    public List<Stock> getStockByPortfolioId(Integer id) {
        return stockRepo.findByPortfolioID(id);
    }

    public Stock getStockByTickerAndPortfolioId(String ticker, Integer portfolioID) {
        Optional<Stock> existingStock = stockRepo.findById(new StockId(ticker, portfolioID));
        if (existingStock.isEmpty()) {
            throw new StockIllegalArgumentException("Stock " + ticker + " does not exist in portfolio " + portfolioID + ".");
        }

        return existingStock.get();
    }

    public NewStockDTO saveNewStockRequest(NewStockRequest req) {
        // Check if portfolio exists
        Optional<Portfolio> existingPortfolio = portfolioRepo.findById(req.getPortfolioID());
        if (existingPortfolio.isEmpty()) {
            throw new StockIllegalArgumentException("Portfolio with ID " + req.getPortfolioID() + " does not exist.");
        }
        Portfolio portfolio = existingPortfolio.get();
        StockId stockId = new StockId(req.getTickerSymbol(), req.getPortfolioID());
<<<<<<< HEAD
        StockData stockData = stockDataRepository.findByTickerSymbol(req.getTickerSymbol());
        logger.info("Received stock quote from DB:\nSymbol: {}\nPrice: {}\nChange %: {}", stockData.getTickerSymbol(), stockData.getPrice(), stockData.getChangePercent());
        logger.info("User wants {} x{}", req.getTickerSymbol(), req.getQty());
        logger.info("Portfolio cash: {}\nCash to purchase: {}", existingPortfolio.get().getCash(), existingPortfolio.get().getCash() - (stockData.getPrice() * req.getQty()));
        //check if user can afford stock purchase
        if (existingPortfolio.get().getCash() - (stockData.getPrice() * req.getQty()) < 0)
=======

        // Check if stock already exists
        Optional<Stock> existingStock = stockRepo.findById(stockId);
        if (existingStock.isPresent()) {
            throw new StockIllegalArgumentException("Stock " + req.getTickerSymbol() + " already exists in portfolio " +
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
>>>>>>> hannah_branch
            throw new InsufficientCashException("Your portfolio does not have enough cash to purchase x" + req.getQty()
                    + " of " + req.getTickerSymbol());

        // Check if stock already exists
        Optional<Stock> existingStock = stockRepo.findById(stockId);
        Stock stock;
        if (existingStock.isPresent()) {
            stock = existingStock.get();
            Double newAvgPrice = calculateAveragePrice(
                    stock.getAvgPrice(), stock.getQty(),
                    stockData.getPrice(), req.getQty()
            );
                    stock.setQty(stock.getQty() + req.getQty());
            logger.debug("Buying more stock: {}", stock);
        } else {
            stock = new Stock();
            stock.setTickerSymbol(req.getTickerSymbol());
            stock.setPortfolioID(req.getPortfolioID());
            stock.setQty(req.getQty());
            stock.setCurrentPrice(stockData.getPrice());
            stock.setAvgPrice(stockData.getPrice()); // Initial purchase sets avg price to current price
            stock.setChangePercent(stockData.getChangePercent());
            logger.debug("Saving stock: {}", stock);
        }
        stockRepo.save(stock);
        //update portfolio cash
        existingPortfolio.get().setCash(existingPortfolio.get().getCash() - (stockData.getPrice() * req.getQty()));
        portfolioRepo.save(existingPortfolio.get());
        //record new transaction
        Transaction transaction = new Transaction();
        transaction.setPortfolioID(portfolio.getPortfolioID());
        transaction.setTickerSymbol(stockData.getTickerSymbol());
        transaction.setPrice(stockData.getPrice());
        transaction.setQty(req.getQty());
        transaction.setSell(false);
        transaction.setTransactionTime(ZonedDateTime.now(ZoneId.systemDefault()));
        transactionRepository.save(transaction);
        //return new/updated stock data
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
            throw new StockIllegalArgumentException("Stock " + ticker + " does not exist in portfolio " + portfolioID +
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

    public double sellStockById(SellStockRequest req) {
        Optional<Stock> existingStock = stockRepo.findById(new StockId(req.getTickerSymbol(), req.getPortfolioID()));
        if (existingStock.isEmpty()) {
            throw new IllegalArgumentException("Stock " + req.getTickerSymbol() + " does not exist in this portfolio" +
                    ". Please add the stock first.");
        }
        Stock stock = existingStock.get();
        Optional<Portfolio> existingPortfolio = portfolioRepo.findById(req.getPortfolioID());
        if (existingPortfolio.isEmpty()) {
            throw new IllegalArgumentException("Portfolio with ID " + req.getPortfolioID() + " does not exist.");
        }
        Portfolio portfolio = existingPortfolio.get();
        logger.info("Selling x amount {}", req.getQty());
        logger.info("At x price {}", stock.getCurrentPrice());
        logger.info("Selling stock for {}", req.getQty() * stock.getCurrentPrice());
        double profit = req.getQty() * stock.getCurrentPrice();
        portfolio.setCash(portfolio.getCash() + profit);
        portfolioRepo.save(portfolio);
        if (req.getQty().equals(stock.getQty())) {
            stockRepo.deleteById(new StockId(req.getTickerSymbol(), req.getPortfolioID()));
        } else {
            stock.setQty(stock.getQty() - req.getQty());
            stockRepo.save(stock);
        }
        Transaction transaction = new Transaction();
        transaction.setPortfolioID(portfolio.getPortfolioID());
        transaction.setTickerSymbol(stock.getTickerSymbol());
        transaction.setPrice(stock.getCurrentPrice());
        transaction.setQty(req.getQty());
        transaction.setSell(true);
        transaction.setTransactionTime(ZonedDateTime.now(ZoneId.systemDefault()));
        transactionRepository.save(transaction);
        return profit;
    }

    public void deleteStockById(String ticker, Integer portfolioID) {
        Optional<Stock> existingStock = stockRepo.findById(new StockId(ticker, portfolioID));
        if (existingStock.isEmpty()) {
            throw new StockIllegalArgumentException("Stock " + ticker + " does not exist in portfolio " + portfolioID + ".");
        }

        stockRepo.deleteById(new StockId(ticker, portfolioID));
    }

    private Double calculateAveragePrice(Double currentAvgPrice, Integer currentQty, Double newPrice, Integer newQty) {
        if (currentQty == 0) {
            // Initial purchase - average price is the purchase price
            return newPrice;
        }

        // Calculate weighted average price
        Double currentValue = currentAvgPrice * currentQty;
        Double newValue = newPrice * newQty;
        Double totalValue = currentValue + newValue;
        Integer totalQty = currentQty + newQty;

        return totalValue / totalQty;
    }
}
