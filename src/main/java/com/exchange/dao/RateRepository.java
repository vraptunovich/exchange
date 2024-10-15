package com.exchange.dao;

import com.exchange.dao.model.ExchangeRateDto;
import org.springframework.data.repository.CrudRepository;

public interface RateRepository extends CrudRepository<ExchangeRateDto, Long> {
}