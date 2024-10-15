package com.exchange.service;

import com.exchange.cache.CurrencyCache;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class Scheduler {

    private final ExchangeRatesService exchangeRatesService;
    private final CurrencyCache currencyCache;


    @Scheduled(fixedRate = 3600000)
    public void scheduled() {
        currencyCache.invalidateAll();
        exchangeRatesService.refreshCurrencyRate();
    }


}
