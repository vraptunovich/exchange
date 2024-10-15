package com.exchange.rest;

import com.exchange.models.Currency;
import com.exchange.service.ExchangeRatesService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class ExchangeRestApi {

    private final ExchangeRatesService exchangeRatesService;


    @GetMapping("/api/exchangeRate")
    public Currency getExchangeRate(@RequestParam(defaultValue = "", required = false) String baseCurrency) {
        return exchangeRatesService.getCurrencyRate(baseCurrency);
    }

    @GetMapping("/api/exchangeRate/currencies")
    public List<String> getCurrencies() {
        return exchangeRatesService.getCurrencies();
    }

    @PostMapping("/api/exchangeRate/add/currencies")
    public void addCurrencies(@RequestParam(defaultValue = "EUR", required = false) String currencies) {
        exchangeRatesService.addCurrencies(Arrays.stream(currencies.split(",")).toList());
    }


}
