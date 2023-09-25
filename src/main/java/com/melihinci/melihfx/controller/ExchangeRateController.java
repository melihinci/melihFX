package com.melihinci.melihfx.controller;

import com.melihinci.melihfx.model.Currency;
import com.melihinci.melihfx.service.GlobalCurrencies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.ArrayList;
import java.util.List;

//@Api(value = "Exchange Rates Api documentation")
public class ExchangeRateController {

    @Autowired
    GlobalCurrencies globalCurrencies;

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

    @GetMapping(path = "/exchangeRates/{sourceCurrencyCode}")
//    @ApiOperation(value = "Exchange rates info viewing method by only source currency")
    public ResponseEntity<List<Currency>> exchangeRates(@PathVariable String sourceCurrencyCode) {
        List<Currency> result=new ArrayList<>();
        globalCurrencies.getCurrencies().forEach( (key, currency) -> {
            if(key.startsWith(sourceCurrencyCode)){
                result.add(currency);
            }
        } );
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PostMapping(path = "/exchangeRates")
//    @ApiOperation(value = "Exchange rate listing method")
    public ResponseEntity<List<Currency>> exchangeRates(@RequestParam String sourceCurrencyCode, @RequestParam List<String> targetCurrencyCodes) throws NullPointerException{
        List<Currency> sourceResult=exchangeRates(sourceCurrencyCode).getBody();
        List<Currency> result=new ArrayList<>();
        sourceResult.forEach(currency->{
            if(targetCurrencyCodes.contains(currency.getTarget())){
                result.add(currency);
            }
        });
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
