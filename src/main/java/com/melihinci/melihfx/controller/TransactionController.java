package com.melihinci.melihfx.controller;

import com.melihinci.melihfx.model.Transaction;
import com.melihinci.melihfx.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
//@Api(value = "Exchange transaction api documentation")
public class TransactionController {

    @Autowired
    TransactionService transactionService;

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<String> handleException(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
    }

    @ExceptionHandler(NullPointerException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<String> handleException(NullPointerException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @PostMapping(path = "/newTransaction")
    // @ApiOperation(value = "New Transaction adding method")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Long> newTransaction(@RequestParam String sourceCurrencyCode,@RequestParam String targetCurrencyCode,@RequestParam float amount) {
        Transaction transaction=new Transaction();
        transaction.setAmount(amount);
        transaction.setCurrencyExchangeCode( sourceCurrencyCode + targetCurrencyCode);
        return new ResponseEntity<>(transactionService.createTransaction(transaction),HttpStatus.CREATED);
    }

    @PostMapping(path = "/createTransactions")
    // @ApiOperation(value = "Multiple transaction adding method")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<List<Long>> createTransactions(@RequestParam String sourceCurrencyCode, @RequestParam List<String> targetCurrencyCodes, @RequestParam float amount) {
        List<Long> insertedTransactionIds = new ArrayList<>();
        targetCurrencyCodes.forEach(targetCurrencyCode -> {
            Transaction transaction = new Transaction();
            transaction.setAmount(amount);
            transaction.setCurrencyExchangeCode(sourceCurrencyCode + targetCurrencyCode);
            insertedTransactionIds.add(transactionService.createTransaction(transaction));
        });
        return new ResponseEntity<>(insertedTransactionIds, HttpStatus.CREATED);
    }

    @PostMapping(path = "/asyncTransaction")
    // @ApiOperation(value = "New Transaction adding async method")
    @ResponseStatus(HttpStatus.CREATED)
    @Async
    public ResponseEntity<Long> asyncTransaction(@RequestParam String sourceCurrencyCode,@RequestParam String targetCurrencyCode,@RequestParam float amount) {
        CompletableFuture.runAsync(()->
            newTransaction(sourceCurrencyCode,targetCurrencyCode,amount)
        );
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
