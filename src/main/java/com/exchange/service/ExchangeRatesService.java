package com.exchange.service;

import com.exchange.cache.CurrencyCache;
import com.exchange.dao.model.CurrencyDto;
import com.exchange.dao.CurrencyRepository;
import com.exchange.models.Currency;
import com.exchange.models.ExchangeRateMapper;
import com.exchange.models.ExchangeRateResponse;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class ExchangeRatesService {

    private final RestTemplate restTemplate;
    private final CurrencyCache currencyCache;
    private final CurrencyRepository currencyRepository;
    private final ExchangeRateMapper mapper = ExchangeRateMapper.INSTANCE;

    @Value("${exchange.rates.api.url}")
    private String exchangeRateApiUrl;

    private static final String BASE_CURRENCY_PARAM = "base";


    public List<String> getCurrencies() {
        return StreamSupport.stream(currencyRepository.findAll().spliterator(), false)
                .map(CurrencyDto::getCurrencyCode).distinct().toList();
    }

    public void addCurrencies(List<String> currencies) {
        for (String currencyCode : currencies) {
            refreshCurrencyRate(currencyCode);
        }
    }

    public void refreshCurrencyRate() {
        for (String currencyCode : getCurrencies()) {
            refreshCurrencyRate(currencyCode);
        }
    }

    public void refreshCurrencyRate(String currencyCode) {
        Currency currency = mapper.toCurrency(
                //---------- exchangeratesapi works only for one currency for free account
                restTemplate.getForObject(buildUrl("EUR")
                        , ExchangeRateResponse.class));

        if (isValid(currency)) {
            if (StringUtils.isNotEmpty(currencyCode) && !currency.getCurrencyCode().equals(currencyCode)) {
                currency.setCurrencyCode(currencyCode);
            }
            //---------------------------

            currencyCache.put(currencyCode, currency);
        }
    }

    public Currency getCurrencyRate(String currencyCode) {
        if (!getCurrencies().contains(currencyCode)) {
            return Currency.builder().build();
        }

        return currencyCache.get(currencyCode);
    }

    private String buildUrl(String baseCurrency) {
        String url = exchangeRateApiUrl;
        if (StringUtils.isNotEmpty(baseCurrency)) {
            url += "&" + BASE_CURRENCY_PARAM + "=" + baseCurrency.toUpperCase();
        }
        return url;
    }

    private boolean isValid(Currency currency) {
        return currency != null
                && currency.getCurrencyCode() != null
                && !currency.getCurrencyCode().isEmpty()
                && currency.getRates() != null
                && !currency.getRates().isEmpty();
    }


}
