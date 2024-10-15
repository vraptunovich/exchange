CREATE TABLE base_currency
(
    id       BIGSERIAL PRIMARY KEY,
    currency_code     VARCHAR  NOT NULL,
    date date  NOT NULL,
    timestamp timestamp NOT NULL
)