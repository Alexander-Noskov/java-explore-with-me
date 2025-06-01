CREATE TABLE IF NOT EXISTS users
(
    id    SERIAL PRIMARY KEY,
    email VARCHAR(256) UNIQUE NOT NULL,
    name  VARCHAR(256)        NOT NULL
);

CREATE TABLE IF NOT EXISTS categories
(
    id   SERIAL PRIMARY KEY,
    name VARCHAR(64) UNIQUE NOT NULL
);

CREATE TABLE IF NOT EXISTS events
(
    id                 SERIAL PRIMARY KEY,
    annotation         VARCHAR(2048) NOT NULL,
    category_id        BIGINT        NOT NULL,
    initiator_id       BIGINT        NOT NULL,
    description        VARCHAR(8192) NOT NULL,
    event_date         TIMESTAMP     NOT NULL CHECK (event_date >= (CURRENT_TIMESTAMP + INTERVAL '2 hours')),
    created_on         TIMESTAMP     NOT NULL,
    published_on       TIMESTAMP,
    location_lat       DECIMAL(9, 6) NOT NULL,
    location_lon       DECIMAL(9, 6) NOT NULL,
    paid               BOOLEAN       NOT NULL,
    participant_limit  INTEGER       NOT NULL,
    request_moderation BOOLEAN       NOT NULL,
    title              VARCHAR(128)  NOT NULL,
    state              VARCHAR(16)   NOT NULL,
    CONSTRAINT fk_events_category FOREIGN KEY (category_id) REFERENCES categories (id) ON DELETE RESTRICT,
    CONSTRAINT fk_events_initiator FOREIGN KEY (initiator_id) REFERENCES users (id) ON DELETE RESTRICT
);

CREATE TABLE IF NOT EXISTS compilations
(
    id     SERIAL PRIMARY KEY,
    pinned BOOLEAN     NOT NULL,
    title  VARCHAR(64) NOT NULL
);

CREATE TABLE IF NOT EXISTS compilation_events
(
    compilation_id BIGINT NOT NULL,
    event_id       BIGINT,
    PRIMARY KEY (compilation_id, event_id),
    CONSTRAINT fk_compilation FOREIGN KEY (compilation_id) REFERENCES compilations (id) ON DELETE CASCADE,
    CONSTRAINT fk_event FOREIGN KEY (event_id) REFERENCES events (id) ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS requests
(
    id           SERIAL PRIMARY KEY,
    event_id     BIGINT      NOT NULL,
    requester_id BIGINT      NOT NULL,
    status       VARCHAR(16) NOT NULL,
    created      TIMESTAMP   NOT NULL,
    CONSTRAINT fk_requests_event FOREIGN KEY (event_id) REFERENCES events (id) ON DELETE SET NULL,
    CONSTRAINT fk_requests_requester FOREIGN KEY (requester_id) REFERENCES users (id) ON DELETE SET NULL,
    CONSTRAINT uq_request_event_requester UNIQUE (event_id, requester_id)
);