package com.exchange.models;

import lombok.Builder;
import lombok.Data;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;

@Data
@Builder
public class Currency {
    String currencyCode;
    Date date;
    Timestamp timestamp;
    List<ExchangeRate> rates;
}


