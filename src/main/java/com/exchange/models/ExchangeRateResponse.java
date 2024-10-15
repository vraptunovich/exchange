package com.exchange.models;

import lombok.*;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.Map;

@AllArgsConstructor
@RequiredArgsConstructor
@Builder
@Data
public class ExchangeRateResponse {
    private boolean success;
    private Timestamp timestamp;
    private String base;
    private Date date;
    private Map<String, Double> rates;
}
