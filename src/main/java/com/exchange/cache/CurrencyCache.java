package com.exchange.cache;

import com.exchange.dao.model.CurrencyDto;
import com.exchange.dao.CurrencyRepository;
import com.exchange.dao.model.ExchangeRateDto;
import com.exchange.dao.RateRepository;
import com.exchange.models.Currency;
import com.exchange.models.ExchangeRateMapper;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class CurrencyCache {

    private final CurrencyRepository currencyRepository;
    private final RateRepository rateRepository;
    private final ExchangeRateMapper mapper = ExchangeRateMapper.INSTANCE;

    private final static Cache<String, Currency> cache = CacheBuilder.newBuilder()
            //.expireAfterWrite(60, TimeUnit.MINUTES) //optional
            .build();


    private String getKey(String cur) {
        return cur;
    }

    public void put(String code, Currency currency) {
        cache.put(getKey(code), currency);

        saveToDb(currency);
    }

    private void saveToDb(Currency currency) {
        CurrencyDto currencyDto = currencyRepository.save(mapper.toCurrencyDto(currency));

        rateRepository.saveAll(currency.getRates().stream().map(r -> {
            ExchangeRateDto dto = mapper.toRateDto(r);
            dto.setBaseId(currencyDto.getId());
            return dto;
        }).toList());
    }

    public Currency get(String currentCode) {
        return cache.getIfPresent(getKey(currentCode));
    }

    public void invalidateAll() {
        cache.invalidateAll();
    }

}