package com.exchange.service;

import com.exchange.cache.CurrencyCache;
import com.exchange.dao.model.CurrencyDto;
import com.exchange.dao.CurrencyRepository;
import com.exchange.models.Currency;
import com.exchange.models.ExchangeRate;
import com.exchange.models.ExchangeRateResponse;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.RestTemplate;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@SpringBootTest
public class ExchangeRatesServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private CurrencyCache currencyCache;

    @Mock
    private CurrencyRepository currencyRepository;

    @InjectMocks
    private ExchangeRatesService exchangeRatesService;


    @Test
    public void testGetCurrencies() {
        // given
        CurrencyDto currencyDto1 = new CurrencyDto();
        currencyDto1.setCurrencyCode("USD");

        CurrencyDto currencyDto2 = new CurrencyDto();
        currencyDto2.setCurrencyCode("EUR");

        // when
        when(currencyRepository.findAll()).thenReturn(Arrays.asList(currencyDto1, currencyDto2));

        List<String> currencies = exchangeRatesService.getCurrencies();

        // then
        assertThat(currencies).containsExactlyInAnyOrder("USD", "EUR");
    }

    @Test
    public void testAddCurrencies() {

        // given
        String code = "RUB", baseCode = "USD";
        double rateValue = 1.1D;
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        ExchangeRateResponse response = ExchangeRateResponse.builder()
                .success(true)
                .base(baseCode)
                .date(new Date(timestamp.getTime()))
                .rates(Map.of(code, rateValue))
                .build();

        when(restTemplate.getForObject(anyString(), eq(ExchangeRateResponse.class))).thenReturn(response);

        // when
        exchangeRatesService.addCurrencies(List.of("USD","EUR"));

        // then
        verify(restTemplate, times(2)).getForObject(anyString(), eq(ExchangeRateResponse.class));
        verify(currencyCache, times(2)).put(anyString(), any(Currency.class));
    }

    @Test
    public void testRefreshCurrencyRate() {
        // given
        String code = "RUB", baseCode = "USD";
        double rateValue = 1.1D;
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        ExchangeRateResponse response = ExchangeRateResponse.builder()
                .success(true)
                .base(baseCode)
                .date(new Date(timestamp.getTime()))
                .rates(Map.of(code, rateValue))
                .build();


        when(restTemplate.getForObject(anyString(), eq(ExchangeRateResponse.class))).thenReturn(response);

        // when
        exchangeRatesService.refreshCurrencyRate(baseCode);

        // then
        verify(currencyCache, times(1)).put(eq(baseCode), any(Currency.class));
    }

    @Test
    public void testRefreshCurrencyRate_withInvalidResponse() {
        // given
        when(restTemplate.getForObject(anyString(), eq(ExchangeRateResponse.class))).thenReturn(null);

        // when
        exchangeRatesService.refreshCurrencyRate("USD");

        // then
        verify(currencyCache, never()).put(anyString(), any(Currency.class));
    }

    @Test
    public void testGetCurrencyRate() {
        // given
        CurrencyDto currencyDto = new CurrencyDto();
        currencyDto.setCurrencyCode("USD");
        when(currencyRepository.findAll()).thenReturn(List.of(currencyDto));
        double rate = 1.1D;
        Currency cachedCurrency = Currency.builder().currencyCode("USD").build();

        ExchangeRate exchangeRate = ExchangeRate.builder().currencyCode("RUB").rate(rate).build();
        cachedCurrency.setRates(List.of(exchangeRate));

        when(currencyCache.get("USD")).thenReturn(cachedCurrency);

        // when
        Currency currency = exchangeRatesService.getCurrencyRate("USD");

        // then
        assertThat(currency.getCurrencyCode()).isEqualTo("USD");
    }


}