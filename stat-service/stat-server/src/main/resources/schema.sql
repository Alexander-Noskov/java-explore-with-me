CREATE TABLE IF NOT EXISTS endpointhits
(
    id        SERIAL PRIMARY KEY,
    app       VARCHAR(256),
    uri       VARCHAR(512),
    ip        VARCHAR(16),
    timestamp TIMESTAMP
);
