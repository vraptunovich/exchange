package com.exchange.dao.model;

import jakarta.persistence.*;
import lombok.Data;

import java.sql.Date;
import java.sql.Timestamp;

@Data
@Entity(name = "base_currency")
public class CurrencyDto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;
    @Column( name = "currency_code")
    String currencyCode;
    Date date;
    Timestamp timestamp;
}


