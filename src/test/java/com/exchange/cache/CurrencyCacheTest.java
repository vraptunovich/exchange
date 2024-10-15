package com.exchange.cache;


import com.exchange.dao.model.CurrencyDto;
import com.exchange.dao.CurrencyRepository;
import com.exchange.dao.model.ExchangeRateDto;
import com.exchange.dao.RateRepository;
import com.exchange.models.Currency;
import com.exchange.models.ExchangeRate;
import com.exchange.models.ExchangeRateMapper;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
public class CurrencyCacheTest {

    @Mock
    private CurrencyRepository currencyRepository;

    @Mock
    private RateRepository rateRepository;

    @InjectMocks
    private CurrencyCache currencyCache;

    private ExchangeRateMapper mapper = ExchangeRateMapper.INSTANCE;


    @Test
    public void shouldStoreCurrencyInCacheAndSaveToDb() {
        //given
        Currency currency = Currency.builder().currencyCode("USD").build();

        ExchangeRate rate = ExchangeRate.builder().currencyCode("RUB").rate(1.1D).build();
        currency.setRates(List.of(rate));

        CurrencyDto currencyDto = new CurrencyDto();
        currencyDto.setId(1L);

        //when
        when(currencyRepository.save(any(CurrencyDto.class))).thenReturn(currencyDto);

        currencyCache.put("USD", currency);

        //then
        Currency cachedCurrency = currencyCache.get("USD");
        assertThat(cachedCurrency).isNotNull();
        assertThat(cachedCurrency.getCurrencyCode()).isEqualTo("USD");

        verify(currencyRepository, times(1)).save(any(CurrencyDto.class));
        verify(rateRepository, times(1)).saveAll(anyList());
    }

    @Test
    public void shouldReturnCurrencyFromCache() {
        //given
        ExchangeRate rate = ExchangeRate.builder().currencyCode("RUB").rate(1.1D).build();
        Currency currency = Currency.builder().currencyCode("EUR").rates(List.of(rate)).build();
        CurrencyDto currencyDto = mapper.toCurrencyDto(currency);
        currencyDto.setId(1L);

        //when
        when(currencyRepository.save(any(CurrencyDto.class))).thenReturn(currencyDto);

        currencyCache.put("EUR", currency);

        Currency cachedCurrency = currencyCache.get("EUR");

        //then
        assertThat(cachedCurrency).isNotNull();
        assertThat(cachedCurrency.getCurrencyCode()).isEqualTo("EUR");
    }

    @Test
    public void shouldClearCache() {
        //given
        ExchangeRate rate = ExchangeRate.builder().currencyCode("RUB").rate(1.1D).build();
        Currency currency = Currency.builder().currencyCode("GBP").rates(List.of(rate)).build();
        CurrencyDto currencyDto = mapper.toCurrencyDto(currency);
        currencyDto.setId(1L);
        //when
        when(currencyRepository.save(any(CurrencyDto.class))).thenReturn(currencyDto);

        currencyCache.put("GBP", currency);

        currencyCache.invalidateAll();

        //then
        Currency cachedCurrency = currencyCache.get("GBP");
        assertThat(cachedCurrency).isNull();
    }

    @Test
    public void shouldSaveCurrencyAndRatesToDb() {
        // given
        double rate = 1.1D;
        Currency currency = Currency.builder().currencyCode("USD").build();

        ExchangeRate exchangeRate = ExchangeRate.builder().currencyCode("RUB").rate(rate).build();
        currency.setRates(List.of(exchangeRate));

        CurrencyDto currencyDto = new CurrencyDto();
        currencyDto.setId(1L);
        // when
        when(currencyRepository.save(any(CurrencyDto.class))).thenReturn(currencyDto);

        currencyCache.put("USD", currency);

        //then
        ArgumentCaptor<List<ExchangeRateDto>> rateCaptor = ArgumentCaptor.forClass(List.class);
        verify(rateRepository, times(1)).saveAll(rateCaptor.capture());

        List<ExchangeRateDto> savedRates = rateCaptor.getValue();
        assertThat(savedRates).hasSize(1);
        assertThat(savedRates.get(0).getBaseId()).isEqualTo(1L);
        assertThat(savedRates.get(0).getRate()).isEqualTo(rate);
    }
}