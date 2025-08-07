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
    public ResponseEntity<List<Stock>> getByPortfolioID(@PathVariable Integer portfolioID) {
        List<Stock> stockList = stockService.getStockByPortfolioId(portfolioID);
        return ResponseEntity.ok(stockList);
    }

    @GetMapping("/{portfolioID}/{ticker}")
    public ResponseEntity<Stock> getById(@PathVariable Integer portfolioID, @PathVariable String ticker) {
        Stock stock = stockService.getStockByTickerAndPortfolioId(ticker, portfolioID);
        return ResponseEntity.ok(stock);
    }

    @PostMapping("/purchase")
    public ResponseEntity<NewStockDTO> create(@RequestBody NewStockRequest req)
    {
        logger.info("Received request to purchase stock {}", req);
        NewStockDTO newStockDTO = stockService.saveNewStockRequest(req);
        return ResponseEntity.status(HttpStatus.CREATED).body(newStockDTO);
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
    public ResponseEntity<Stock> update(@PathVariable Integer portfolioID, @PathVariable String ticker, @RequestBody UpdateStockRequest req) {
        Stock stock = stockService.updateStockRequest(ticker, portfolioID, req);
        return ResponseEntity.ok(stock);
    }


    @DeleteMapping("/{portfolioID}/{ticker}")
    public ResponseEntity<Void> delete(@PathVariable Integer portfolioID, @PathVariable String ticker) {
        stockService.deleteStockById(ticker, portfolioID);
        return ResponseEntity.noContent().build();
    }
}
