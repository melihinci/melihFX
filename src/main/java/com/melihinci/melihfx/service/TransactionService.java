package com.melihinci.melihfx.service;

import com.melihinci.melihfx.model.Transaction;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public interface TransactionService {

    public long createTransaction(Transaction transaction);

    public List<Transaction> getTransactionsFilteredByNotNulls(String source, String target, Double rateLowerThan, Double rateHigherThan, Date afterThan, Date beforeThan);

    public Transaction getTransaction(long transactionId);

}
