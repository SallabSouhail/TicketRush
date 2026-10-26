-- Events managed by event-service: one row per event, whatever its status.
-- Business rules enforced here are those that never depend on the current time.
-- Time-dependent rules (start in the future, sales not started yet)
-- and the timezone validity are checked by the application only.

CREATE TABLE event (
    id                  uuid          PRIMARY KEY,
    title               varchar(150)  NOT NULL,
    description         text,
    category            varchar(32)   NOT NULL,
    capacity            integer       NOT NULL,
    venue_name          varchar(150)  NOT NULL,
    city                varchar(100)  NOT NULL,
    country             char(2)       NOT NULL,
    timezone            varchar(64)   NOT NULL,
    start_at            timestamptz   NOT NULL,
    end_at              timestamptz   NOT NULL,
    sales_start_at      timestamptz   NOT NULL,
    sales_end_at        timestamptz   NOT NULL,
    status              varchar(16)   NOT NULL,
    organizer_id        uuid          NOT NULL,
    cancellation_reason varchar(500),
    created_at          timestamptz   NOT NULL,
    updated_at          timestamptz   NOT NULL,

    CONSTRAINT ck_event_title_length
        CHECK (char_length(title) BETWEEN 3 AND 150 AND title = btrim(title)),
    CONSTRAINT ck_event_description_length
        CHECK (description IS NULL OR char_length(description) <= 5000),
    CONSTRAINT ck_event_category
        CHECK (category IN ('CONCERT', 'SPORT', 'CONFERENCE', 'THEATER', 'OTHER')),
    CONSTRAINT ck_event_capacity_range
        CHECK (capacity BETWEEN 1 AND 100000),
    CONSTRAINT ck_event_country_format
        CHECK (country ~ '^[A-Z]{2}$'),
    CONSTRAINT ck_event_status
        CHECK (status IN ('DRAFT', 'PUBLISHED', 'CANCELLED')),
    -- sales close before the event starts, the event ends after it starts
    CONSTRAINT ck_event_dates_order
        CHECK (sales_start_at < sales_end_at AND sales_end_at <= start_at AND start_at < end_at),
    -- A cancellation reason exists if and only if the event is cancelled
    CONSTRAINT ck_event_cancellation_reason
        CHECK ((status = 'CANCELLED') = (cancellation_reason IS NOT NULL))
);
