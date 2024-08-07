CREATE SCHEMA IF NOT EXISTS translator;

CREATE TABLE IF NOT EXISTS translator.ip_addresses
(
    id         BIGSERIAL PRIMARY KEY,
    ip_address VARCHAR UNIQUE NOT NULL
);

CREATE TABLE IF NOT EXISTS translator.translate_queries
(
    id                BIGSERIAL PRIMARY KEY,
    ip_address_id     BIGINT  NOT NULL,
    text_to_translate     VARCHAR NOT NULL,
    translated_text VARCHAR NOT NULL,
    FOREIGN KEY (ip_address_id) REFERENCES translator.ip_addresses (id)
);