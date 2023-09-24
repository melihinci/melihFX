package com.melihinci.melihfx.service;

import com.melihinci.melihfx.model.Currency;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface CurrencyUpdateService {
    List<Currency> retrieveCurrencies();
}
