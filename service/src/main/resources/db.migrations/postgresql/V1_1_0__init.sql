DROP TABLE IF EXISTS event;

CREATE TABLE event (
    id VARCHAR(255) NOT NULL,
    name VARCHAR(255) NOT NULL, -- human readable name
    team VARCHAR(255) NOT NULL,
    presentation_name VARCHAR(255) NOT NULL,
    description VARCHAR(255),

    create_datetime TIMESTAMP WITHOUT TIME ZONE,
    update_datetime TIMESTAMP WITHOUT TIME ZONE,

   CONSTRAINT pk_event PRIMARY KEY (id)
);

DROP TABLE IF EXISTS category;

CREATE TABLE category (
    id VARCHAR(255) NOT NULL,
    name VARCHAR(255) NOT NULL, -- human readable name
    presentation_name VARCHAR(255) NOT NULL,
    description VARCHAR(255),
    event_id VARCHAR(255) NOT NULL REFERENCES event(id),

    create_datetime TIMESTAMP WITHOUT TIME ZONE,
    update_datetime TIMESTAMP WITHOUT TIME ZONE,

   CONSTRAINT pk_category PRIMARY KEY (id)
);

DROP TABLE IF EXISTS proposal;

CREATE TABLE proposal (
    id VARCHAR(255) NOT NULL,
    name VARCHAR(255) NOT NULL, -- human readable name
    presentation_name VARCHAR(255) NOT NULL,
    description VARCHAR(255),
    category_id VARCHAR(255) NOT NULL REFERENCES category(id),

    create_datetime TIMESTAMP WITHOUT TIME ZONE,
    update_datetime TIMESTAMP WITHOUT TIME ZONE,

   CONSTRAINT pk_proposal PRIMARY KEY (id)
);

DROP TABLE IF EXISTS root_hash;

CREATE TABLE root_hash (
    event_id VARCHAR(255) NOT NULL,
    root_hash VARCHAR(255) NOT NULL,

   CONSTRAINT pk_root_hash PRIMARY KEY (event_id)
);

--CREATE INDEX idx_stake_registration_slot
--    ON stake_registration (slot);
