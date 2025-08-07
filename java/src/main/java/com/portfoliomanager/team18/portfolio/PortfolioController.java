package com.portfoliomanager.team18.portfolio;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/portfolios")
@CrossOrigin
public class PortfolioController {
    @Autowired
    private PortfolioService portfolioService;

    @GetMapping
    public ResponseEntity<List<Portfolio>> getAllPortfolio() {
        List<Portfolio> portfolioList = portfolioService.getAllPortfolio();
        return ResponseEntity.ok(portfolioList);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Portfolio> getPortfolioById(@PathVariable Integer id) {
        Portfolio portfolio = portfolioService.getPortfolioById(id);
        return ResponseEntity.ok(portfolio);
    }

    @PostMapping
    public ResponseEntity<Portfolio> createPortfolio(@RequestBody NewPortfolioRequest req) {
        Portfolio portfolio = portfolioService.saveNewPortfolioRequest(req);
        return ResponseEntity.status(HttpStatus.CREATED).body(portfolio);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Portfolio> updatePortfolio(@PathVariable Integer id, @RequestBody UpdatePortfolioRequest req) {
        Portfolio portfolio = portfolioService.updatePortfolioRequest(id, req);
        return ResponseEntity.ok(portfolio);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePortfolio(@PathVariable Integer id) {
        portfolioService.deletePortfolioById(id);
        return ResponseEntity.noContent().build();
    }
}
