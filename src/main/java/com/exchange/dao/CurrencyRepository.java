package com.exchange.dao;

import com.exchange.dao.model.CurrencyDto;
import org.springframework.data.repository.CrudRepository;

public interface CurrencyRepository extends CrudRepository<CurrencyDto, Long> {
}