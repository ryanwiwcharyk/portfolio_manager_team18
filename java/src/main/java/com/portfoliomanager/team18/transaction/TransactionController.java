package com.portfoliomanager.team18.transaction;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@CrossOrigin
public class TransactionController {
    @Autowired
    private TransactionService transactionService;

    @GetMapping("/{portfolioID}")
    public List<Transaction> getByPortfolioID(@PathVariable Integer portfolioID) {
        return transactionService.getByPortfolioID(portfolioID);
    }

}
