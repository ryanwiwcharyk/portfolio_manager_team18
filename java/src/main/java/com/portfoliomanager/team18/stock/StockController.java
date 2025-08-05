package com.portfoliomanager.team18.stock;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stocks")
@CrossOrigin
public class StockController {
    @Autowired
    private StockService stockService;

    @GetMapping("/{portfolioID}")
    public List<Stock> getByPortfolioID(@PathVariable Integer portfolioID) {
        return stockService.getStockByPortfolioId(portfolioID);
    }

    @GetMapping("/{portfolioID}/{ticker}")
    public Stock getById(@PathVariable Integer portfolioID, @PathVariable String ticker) {
        return stockService.getStockByTickerAndPortfolioId(ticker, portfolioID);
    }

    @PostMapping
    public NewStockDTO create(@RequestBody NewStockRequest req) {
        return stockService.saveNewStockRequest(req);
    }

    @PutMapping("/{portfolioID}/{ticker}")
    public Stock update(@PathVariable Integer portfolioID, @PathVariable String ticker, @RequestBody UpdateStockRequest req) {
        return stockService.updateStockRequest(ticker, portfolioID, req);
    }

    @DeleteMapping("/{portfolioID}/{ticker}")
    public void delete(@PathVariable Integer portfolioID, @PathVariable String ticker) {
        stockService.deleteStockById(ticker, portfolioID);
    }
}
