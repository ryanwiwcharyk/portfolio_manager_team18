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

    @GetMapping(path = "/{portfolioId}")
    public Portfolio getByPortfolioId(@PathVariable Integer portfolioId) { return portfolioService.getByPortfolioId(portfolioId); }

    @GetMapping
    public List<Portfolio> getAll() {
        return portfolioService.getAll();
    }

    @PostMapping
    public Portfolio create(@RequestBody Portfolio p) {
        return portfolioService.save(p);
    }
}
