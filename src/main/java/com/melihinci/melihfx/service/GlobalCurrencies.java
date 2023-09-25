package com.melihinci.melihfx.service;

import com.melihinci.melihfx.model.Currency;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class GlobalCurrencies {

    @Autowired
    CurrencyUpdateService currencyUpdateService;


    private Map<String,Currency> currencies = new HashMap<>();

    public GlobalCurrencies() {
        this.currencies.put("USDUSD",new Currency("USDUSD", "USD", "USD", LocalDateTime.MIN, 1));
    }

    public Map<String,Currency> getCurrencies() {
        return this.currencies;
    }

    @Scheduled(fixedRate = 100000)
    public void updateCurrencies() {
        List<Currency> currenciesBuffer = currencyUpdateService.retrieveCurrencies();
        if (!currenciesBuffer.isEmpty()) {
            syncronizedUpdate(currenciesBuffer);
        }
    }

    private synchronized void syncronizedUpdate(List<Currency> currenciesBuffer) {
        this.currencies.clear();
        this.currencies.putAll(currenciesBuffer.stream().collect(Collectors.toMap(s -> s.getExchangeCode(), s -> s)));
    }
}
