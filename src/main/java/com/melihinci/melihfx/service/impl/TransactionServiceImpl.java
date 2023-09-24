package com.melihinci.melihfx.service.impl;

import com.melihinci.melihfx.model.Currency;
import com.melihinci.melihfx.model.CurrencyLog;
import com.melihinci.melihfx.model.Transaction;
import com.melihinci.melihfx.repository.CurrencyLogRepository;
import com.melihinci.melihfx.repository.TransactionHistoryRepository;
import com.melihinci.melihfx.service.GlobalCurrencies;
import com.melihinci.melihfx.service.TransactionService;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class TransactionServiceImpl implements TransactionService {
    @Autowired
    GlobalCurrencies globalCurrencies;

    @Autowired
    CurrencyLogRepository currencyLogRepository;

    @Autowired
    TransactionHistoryRepository transactionHistoryRepository;

    @PersistenceContext
    private EntityManager entityManager;
    @Override
    public long createTransaction(Transaction transaction) {
        CurrencyLog currencyLog = new CurrencyLog();
        Currency currency = globalCurrencies.getCurrencies().get(transaction.getCurrencyExchangeCode());
        currencyLog.setExchangeRate(currency.getExchangeRate());
        currencyLog.setLogDate(LocalDateTime.now());
        currencyLog.setSource(currency.getSource());
        currencyLog.setTarget(currency.getTarget());
        currencyLogRepository.save(currencyLog);
        transaction.setCurrencyLogId(currencyLog.getId());
        transaction.setTargetAmount(currency.getExchangeRate() * transaction.getAmount());
        transactionHistoryRepository.save(transaction);
        return transaction.getId();
    }
  

    @Override
    public List<Transaction> getTransactionsFilteredByNotNulls(String source, String target, Double rateLowerThan, Double rateHigherThan, Date afterThan, Date beforeThan) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Transaction> criteriaQuery = criteriaBuilder.createQuery(Transaction.class);
        Root<Transaction> root = criteriaQuery.from(Transaction.class);
        Predicate currencyExchangeCodePredicate = null;
        if (target == null & source != null) {
            currencyExchangeCodePredicate = criteriaBuilder.like(root.get("currencyExchangeCode"), source + "%");
        } else if (target != null && source == null) {
            currencyExchangeCodePredicate = criteriaBuilder.like(root.get("currencyExchangeCode"), "%" + target);
        } else if (target != null && source != null) {
            currencyExchangeCodePredicate = criteriaBuilder.like(root.get("currencyExchangeCode"), source + target);
        }

        Join<Transaction, CurrencyLog> currencyLogJoin = root.join("currencyLog");
        Predicate exchangeRatePredicate=null;

        if (rateHigherThan == null & rateLowerThan != null) {
           exchangeRatePredicate  = criteriaBuilder.lessThan(currencyLogJoin.get("exchangeRate"), rateLowerThan);
        } else if (rateHigherThan != null && rateLowerThan == null) {
            exchangeRatePredicate = criteriaBuilder.greaterThan(currencyLogJoin.get("exchangeRate"), rateHigherThan);
        } else if (rateHigherThan != null && rateLowerThan != null) {
            exchangeRatePredicate = criteriaBuilder.between(currencyLogJoin.get("exchangeRate"), rateHigherThan,rateLowerThan);
        }

        Predicate exchangeDatePredicate=null;

        if (afterThan == null & beforeThan != null) {
            exchangeDatePredicate  = criteriaBuilder.lessThan(currencyLogJoin.get("processedDate"), beforeThan);
        } else if (afterThan != null && beforeThan == null) {
            exchangeDatePredicate = criteriaBuilder.greaterThan(currencyLogJoin.get("processedDate"), afterThan);
        } else if (afterThan != null && beforeThan != null) {
            exchangeDatePredicate = criteriaBuilder.between(currencyLogJoin.get("processedDate"), afterThan,beforeThan);
        }

        CriteriaQuery<Transaction> whereQuery = criteriaQuery.where(currencyExchangeCodePredicate,exchangeRatePredicate,exchangeDatePredicate);
        whereQuery.orderBy(criteriaBuilder.asc(root.get("processedDate")));
        return entityManager.createQuery(whereQuery).getResultList();
    }

    @Override
    public List<Transaction> getAllTransactions() {
        List<Transaction> result=new ArrayList<>();
        transactionHistoryRepository.findAll().forEach((each)->{
            result.add(each);
        });
        return result;
    }

    @Override
    public Transaction getTransaction(long transactionId) throws NullPointerException{
        return transactionHistoryRepository.findById(transactionId).get();
    }
}
