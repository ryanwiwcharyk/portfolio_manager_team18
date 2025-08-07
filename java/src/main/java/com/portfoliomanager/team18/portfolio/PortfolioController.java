package com.portfoliomanager.team18.portfolio;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/portfolios")
@CrossOrigin
public class PortfolioController {
    @Autowired
    private PortfolioService portfolioService;

    @GetMapping
    public List<Portfolio> getAllPortfolio() {
        return portfolioService.getAllPortfolio();
    }

    @GetMapping("/{id}")
    public Portfolio getPortfolioById(@PathVariable Integer id) {
        return portfolioService.getPortfolioById(id);
    }

    @PostMapping
    public Portfolio createPortfolio(@RequestBody NewPortfolioRequest req) {
        return portfolioService.saveNewPortfolioRequest(req);
    }

    @PutMapping("/{id}")
    public Portfolio updatePortfolio(@PathVariable Integer id, @RequestBody UpdatePortfolioRequest req) {
        return portfolioService.updatePortfolioRequest(id, req);
    }

    @PutMapping("/{id}/updateCash")
    public Portfolio updatePortfolio(@PathVariable Integer id, @RequestBody Double cash) {
        return portfolioService.updateCash(id, cash);
    }

    @DeleteMapping("/{id}")
    public void deletePortfolio(@PathVariable Integer id) {
        portfolioService.deletePortfolioById(id);
    }
}
