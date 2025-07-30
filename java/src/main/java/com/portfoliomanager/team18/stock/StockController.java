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
        return stockService.getByPortfolioID(portfolioID);
    }

    @PostMapping
    public Stock create(@RequestBody Stock s) {
        return stockService.save(s);
    }

    @DeleteMapping("/{portfolioID}/{ticker}")
    public void delete(@PathVariable Integer portfolioID, @PathVariable String ticker) {
        stockService.delete(ticker, portfolioID);
    }
}
