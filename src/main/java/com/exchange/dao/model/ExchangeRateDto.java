package com.exchange.dao.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity(name = "exchange_rates")
public class ExchangeRateDto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;
    @Column( name = "base_id")
    long baseId;
    @Column( name = "currency_code")
    String currencyCode;
    double rate;


}

