CREATE TABLE exchange_rates
(
    id       BIGSERIAL PRIMARY KEY,
    base_id  BIGINT  NOT NULL,
    currency_code     VARCHAR  NOT NULL,
    rate NUMERIC(15, 6) NOT NULL,
    FOREIGN KEY (base_id) REFERENCES base_currency(id) ON DELETE CASCADE

)