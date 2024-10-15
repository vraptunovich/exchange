package com.exchange.models;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ExchangeRate {
    String currencyCode;
    double rate;


}

