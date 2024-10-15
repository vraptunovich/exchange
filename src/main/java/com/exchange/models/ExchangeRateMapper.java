package com.exchange.models;

import com.exchange.dao.model.CurrencyDto;
import com.exchange.dao.model.ExchangeRateDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Mapper
public interface ExchangeRateMapper {

    ExchangeRateMapper INSTANCE = Mappers.getMapper(ExchangeRateMapper.class);


    @Mapping(source = "base", target = "currencyCode")
    Currency toCurrency(ExchangeRateResponse exchangeRateResponse);

    default List<ExchangeRate> toRates(Map<String, Double> rates) {
        return rates.entrySet().stream().map(e -> ExchangeRate.builder().rate(e.getValue()).currencyCode(e.getKey()).build()).collect(Collectors.toList());
    }

    CurrencyDto toCurrencyDto(Currency currency);

    ExchangeRateDto toRateDto(ExchangeRate rate);

}