package com.melihinci.melihfx.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.melihinci.melihfx.model.Currency;
import com.melihinci.melihfx.model.CurrencyLog;
import com.melihinci.melihfx.repository.CurrencyLogRepository;
import com.melihinci.melihfx.service.CurrencyUpdateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class CurrencyUpdateServiceImpl implements CurrencyUpdateService {

    @Autowired
    CurrencyLogRepository currencyLogRepository;

    @Value("${config.currency.service.url:http://apilayer.net/api/live}")
    private String serviceUrl;

    @Value("${config.currency.service.apikey:b05e25b5552cd02fefb1ea586a439c68}")
    private String serviceApikey;

    @Value("${config.currency.allowed:EUR,JPY,TRY,AED,MYR,SAR,GBP,CHF}")
    private String currencyAllowed;

    @Value("${config.currency.source:USD}")
    private String currencySource;

    @Override
    public List<Currency> retrieveCurrencies() {
        Map<String, Object> params = new HashMap<>();
        params.put("access_key", serviceApikey);
        params.put("currencies", currencyAllowed);
        params.put("source", currencySource);
        RestTemplate restTemplate = new RestTemplate();

        String response = restTemplate.getForObject(serviceUrl, String.class,params);
        ObjectMapper objectMapper = new ObjectMapper();
        List<Currency> currencies = new ArrayList<>();
        try {
            Map<String, Object> jsonObject = objectMapper.readValue(response, Map.class);
            Map<String, Double> quotes = (Map<String, Double>) jsonObject.get("quotes");

            quotes.forEach((currencyId, value) -> {
                Currency currency = new Currency();
                currency.setExchangeCode(currencyId);
                currency.setSource(currencySource);
                currency.setTarget(currencyId.substring(3));
                currency.setTimestamp(LocalDateTime.now());
                currency.setExchangeRate(value);
                currencies.add(currency);
                Currency reverseCurrency = new Currency();
                reverseCurrency.setExchangeCode(currencyId.substring(3)+currencySource);
                reverseCurrency.setSource(currencyId.substring(3));
                reverseCurrency.setTarget(currencySource);
                reverseCurrency.setTimestamp(LocalDateTime.now());
                reverseCurrency.setExchangeRate(1 / value);
                currencies.add(reverseCurrency);
            });
        } catch (JsonProcessingException jpex) {
            new RuntimeException(jpex.getMessage());
        }
        logCurrencies(currencies);
        return currencies;
    }

    @Async
    public void logCurrencies(List<Currency> currencies) {
        List<CurrencyLog> currencyLogs = currencies.stream()
                .map(currency -> new CurrencyLog(currency))
                .collect(Collectors.toList());
        currencyLogRepository.saveAll(currencyLogs);
    }
}
