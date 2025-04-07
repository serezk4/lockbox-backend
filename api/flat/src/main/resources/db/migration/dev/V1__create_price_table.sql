CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE IF NOT EXISTS flat_prices
(
    price_id   UUID PRIMARY KEY        DEFAULT uuid_generate_v4(),
    flat_id    UUID           NOT NULL,
    price      NUMERIC(12, 2) NOT NULL CHECK (price > 0),
    created_at TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_prices_flat FOREIGN KEY (flat_id)
        REFERENCES flats (flat_id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_flat_prices_flat_id ON flat_prices (flat_id);

CREATE OR REPLACE VIEW v_flats_with_latest_price AS
SELECT f.*,
       (SELECT fp.price
        FROM flat_prices fp
        WHERE fp.flat_id = f.flat_id
        ORDER BY fp.created_at DESC
        LIMIT 1) AS latest_price
FROM flats f;