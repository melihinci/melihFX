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

    public final static String CURRENCY_EXCHANGE_CODE = "currencyExchangeCode";
    public final static String CURRENCY_EXCHANGE_RATE = "exchangeRate";
    public final static String CURRENCY_PROCESSED_DATE = "processedDate";

    @Override
    public long createTransaction(Transaction transaction) {
        CurrencyLog currencyLog = new CurrencyLog();
        Currency currency = globalCurrencies.getCurrencies().get(transaction.getCurrencyExchangeCode());
        currencyLog.setExchangeRate(currency.getExchangeRate());
        currencyLog.setSource(currency.getSource());
        currencyLog.setTarget(currency.getTarget());
        currencyLogRepository.save(currencyLog);
        transaction.setCurrencyLogId(currencyLog.getId());
        transaction.setProcessedDate(new Date());
        transaction.setTargetAmount(currency.getExchangeRate() * transaction.getAmount());
        transactionHistoryRepository.save(transaction);
        return transaction.getId();
    }
  

    @Override
    public List<Transaction> getTransactionsFilteredByNotNulls(String source, String target, Double rateLowerThan, Double rateHigherThan, Date afterThan, Date beforeThan) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Transaction> criteriaQuery = criteriaBuilder.createQuery(Transaction.class);
        Root<Transaction> root = criteriaQuery.from(Transaction.class);

        List<Predicate> predicates=new ArrayList<>();
        Predicate currencyExchangeCodePredicate = getCurrencyExchangeCodePredicate(source, target, criteriaBuilder, root);
        if(currencyExchangeCodePredicate!=null) predicates.add(currencyExchangeCodePredicate);
        Join<Transaction, CurrencyLog> currencyLogJoin = root.join("currencyLog");
        Predicate exchangeRatePredicate = getExchangeRatePredicate(rateLowerThan, rateHigherThan, criteriaBuilder, currencyLogJoin);
        if(exchangeRatePredicate!=null) predicates.add(exchangeRatePredicate);
        Predicate exchangeDatePredicate = getExchangeDatePredicate(afterThan, beforeThan, criteriaBuilder, root);
        if(exchangeDatePredicate!=null) predicates.add(exchangeDatePredicate);

        Predicate[] predicateArray= predicates.stream().toArray(Predicate[]::new);
        CriteriaQuery<Transaction> whereQuery = criteriaQuery.where(predicateArray );
        whereQuery.orderBy(criteriaBuilder.asc(root.get(CURRENCY_PROCESSED_DATE)));
        return entityManager.createQuery(whereQuery).getResultList();
    }

    private static Predicate getExchangeDatePredicate(Date afterThan, Date beforeThan, CriteriaBuilder criteriaBuilder, Root<Transaction> root) {
        Predicate exchangeDatePredicate=null;

        if (afterThan == null && beforeThan != null) {
            exchangeDatePredicate  = criteriaBuilder.lessThan(root.get(CURRENCY_PROCESSED_DATE), criteriaBuilder.literal(beforeThan));
        } else if (afterThan != null && beforeThan == null) {
            exchangeDatePredicate = criteriaBuilder.greaterThan(root.get(CURRENCY_PROCESSED_DATE), criteriaBuilder.literal(afterThan));
        } else if (afterThan != null && beforeThan != null) {
            exchangeDatePredicate = criteriaBuilder.between(root.get(CURRENCY_PROCESSED_DATE), criteriaBuilder.literal(afterThan), criteriaBuilder.literal(beforeThan));
        }
        return exchangeDatePredicate;
    }

    private static Predicate getExchangeRatePredicate(Double rateLowerThan, Double rateHigherThan, CriteriaBuilder criteriaBuilder, Join<Transaction, CurrencyLog> currencyLogJoin) {
        Predicate exchangeRatePredicate=null;
        if (rateHigherThan == null && rateLowerThan != null) {
           exchangeRatePredicate  = criteriaBuilder.lessThan(currencyLogJoin.get(CURRENCY_EXCHANGE_RATE), criteriaBuilder.literal(rateLowerThan));
        } else if (rateHigherThan != null && rateLowerThan == null) {
            exchangeRatePredicate = criteriaBuilder.greaterThan(currencyLogJoin.get(CURRENCY_EXCHANGE_RATE), criteriaBuilder.literal(rateHigherThan));
        } else if (rateHigherThan != null && rateLowerThan != null) {
            exchangeRatePredicate = criteriaBuilder.between(currencyLogJoin.get(CURRENCY_EXCHANGE_RATE), criteriaBuilder.literal(rateHigherThan), criteriaBuilder.literal(rateLowerThan));
        }
        return exchangeRatePredicate;
    }

    private static Predicate getCurrencyExchangeCodePredicate(String source, String target, CriteriaBuilder criteriaBuilder, Root<Transaction> root) {
        Predicate currencyExchangeCodePredicate = null;
        if (target == null && source != null) {
            currencyExchangeCodePredicate = criteriaBuilder.like(root.get(CURRENCY_EXCHANGE_CODE), source + "%");
        } else if (target != null && source == null) {
            currencyExchangeCodePredicate = criteriaBuilder.like(root.get(CURRENCY_EXCHANGE_CODE), "%" + target);
        } else if (target != null && source != null) {
            currencyExchangeCodePredicate = criteriaBuilder.like(root.get(CURRENCY_EXCHANGE_CODE), source + target);
        }
        return currencyExchangeCodePredicate;
    }

    @Override
    public Transaction getTransaction(long transactionId) throws NullPointerException{
        return transactionHistoryRepository.findById(transactionId).get();
    }
}
