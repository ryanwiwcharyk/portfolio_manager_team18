package com.portfoliomanager.team18.stock;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StockService {
    @Autowired
    private StockRepository stockRepo;

    public List<Stock> getByPortfolioID(Integer id) {
        return stockRepo.findByPortfolioID(id);
    }

    public Stock save(Stock stock) {
        return stockRepo.save(stock);
    }

    public void delete(String ticker, Integer portfolioID) {
        stockRepo.deleteById(new StockId(ticker, portfolioID));
    }
}
