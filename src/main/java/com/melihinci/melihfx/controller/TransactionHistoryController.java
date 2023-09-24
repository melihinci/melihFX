package com.melihinci.melihfx.controller;

import com.melihinci.melihfx.model.Transaction;
import com.melihinci.melihfx.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
//@Api(value = "Exchange List API documentation")
public class TransactionHistoryController {

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

    @GetMapping(path = "/transaction/{id}")
//    @ApiOperation(value = "Transaction info viewing method")
    public Transaction getTransaction(@PathVariable Long id) {
        return transactionService.getTransaction(id);
    }

    @PostMapping(path = "/listTransactions")
//    @ApiOperation(value = "New Transaction adding method")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<Transaction>> listTransactions(@RequestParam String sourceCurrencyCode, @RequestParam String targetCurrencyCode,
                                                              @RequestParam Double greaterThan, @RequestParam Double lesserThan,
                                                              @RequestParam Date after, @RequestParam Date before) {
        return new ResponseEntity<>(transactionService.getTransactionsFilteredByNotNulls(sourceCurrencyCode, targetCurrencyCode, lesserThan, greaterThan, after, before), HttpStatus.OK);
    }
}
