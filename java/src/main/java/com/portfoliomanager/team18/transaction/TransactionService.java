package com.portfoliomanager.team18.transaction;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TransactionService {
    @Autowired
    private TransactionRepository transactionRepo;

    public List<Transaction> getByPortfolioID(Integer id) {
        return transactionRepo.findByPortfolioID(id);
    }

    public Transaction save(Transaction txn) {
        return transactionRepo.save(txn);
    }
}
