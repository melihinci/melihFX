package com.melihinci.melihfx.controller;

import com.melihinci.melihfx.model.Transaction;
import com.melihinci.melihfx.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.datetime.DateFormatter;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
//@Api(value = "Exchange List API documentation")
public class TransactionHistoryController {

    @Autowired
    TransactionService transactionService;

    public final static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<String> handleException(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<String> handleException(ParseException ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("The data you've sent is incorrect!");
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
    public ResponseEntity<List<Transaction>> listTransactions(@RequestParam(required = false) String sourceCurrencyCode, @RequestParam(required = false) String targetCurrencyCode,
                                                              @RequestParam(required = false) Double greaterThan, @RequestParam(required = false) Double lesserThan,
                                                              @RequestParam(required = false) String after, @RequestParam(required = false) String before) throws ParseException {

        Date afterDate=null, beforeDate=null;
        if (after != null) afterDate = DATE_FORMAT.parse(after);
        if (before != null) beforeDate = DATE_FORMAT.parse(before);
        return new ResponseEntity<>(transactionService
                .getTransactionsFilteredByNotNulls(sourceCurrencyCode, targetCurrencyCode, lesserThan, greaterThan,afterDate, beforeDate),
                HttpStatus.OK);
    }
}
