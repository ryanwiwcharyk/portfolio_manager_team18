package com.portfoliomanager.team18.stock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/stocks")
@CrossOrigin
public class StockController {
    private final Logger logger = LoggerFactory.getLogger(StockController.class);
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

    @PostMapping("/purchase")
    public NewStockDTO create(@RequestBody NewStockRequest req)
    {
        logger.info("Received request to purchase stock {}", req);
        return stockService.saveNewStockRequest(req);
    }

    @PostMapping("/sell")
    public ResponseEntity<Map<String,Object>> sell(@RequestBody SellStockRequest req){
        logger.info("Received request to sell {} shares of {}", req.getQty(), req.getTickerSymbol());
        double profit = stockService.sellStockById(req);
        Map<String, Object> response = Map.of(
                "status", "success",
                "message", "Data processed successfully",
                "updatedCash", profit
        );
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/{portfolioID}/{ticker}")
    public Stock update(@PathVariable Integer portfolioID, @PathVariable String ticker, @RequestBody UpdateStockRequest req) {
        return stockService.updateStockRequest(ticker, portfolioID, req);
    }

//    @DeleteMapping
//    public void delete(@RequestBody SellStockRequest req) {
//        logger.info("Received request to sell {} shares of {}", req.getNumberOfShares(), req.getTickerSymbol());
//        stockService.deleteStockById(ticker, portfolioID);
//    }
}
