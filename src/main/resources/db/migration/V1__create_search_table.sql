
CREATE TABLE IF NOT EXISTS searches (
    search_id     VARCHAR(36)  NOT NULL PRIMARY KEY,
    hotel_id      VARCHAR(64)  NOT NULL,
    check_in      DATE         NOT NULL,
    check_out     DATE         NOT NULL,
    ages_key      VARCHAR(255) NOT NULL,
    registered_at TIMESTAMPTZ  NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_searches_stay
    ON searches (hotel_id, check_in, check_out, ages_key);
