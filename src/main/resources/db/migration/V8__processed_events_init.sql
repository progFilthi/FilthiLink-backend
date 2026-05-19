CREATE TABLE processed_events
(
    event_id     VARCHAR(255)                NOT NULL,
    event_type   VARCHAR(255)                NOT NULL,
    processed_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    version      BIGINT,
    CONSTRAINT pk_processed_events PRIMARY KEY (event_id)
);