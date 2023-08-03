DROP TABLE IF NOT EXISTS event;

CREATE TABLE event (
    id VARCHAR(256) NOT NULL, -- human readable name, should never contain PII data
    team VARCHAR(256) NOT NULL,
    schema_version VARCHAR(256) NOT NULL,
    event_type INT NOT NULL,
    allow_vote_changing BOOL, -- TODO default false
    category_results_while_voting BOOL, -- TODO default false

    voting_power_asset INT,

    start_epoch INT,
    end_epoch INT,

    start_slot BIGINT,
    end_slot BIGINT,

    snapshot_epoch INT,

    absolute_slot BIGINT NOT NULL,

    created_at TIMESTAMP WITHOUT TIME ZONE,
    updated_at TIMESTAMP WITHOUT TIME ZONE,

   CONSTRAINT pk_event PRIMARY KEY (id)
);

DROP TABLE IF NOT EXISTS category;

CREATE TABLE category (
    id VARCHAR(256) NOT NULL, -- human readable name, should never contain PII data
    event_id VARCHAR(256) NOT NULL REFERENCES event(id),
    schema_version VARCHAR(256) NOT NULL,
    gdpr_protection BOOL NOT NULL,

    absolute_slot BIGINT NOT NULL,

    created_at TIMESTAMP WITHOUT TIME ZONE,
    updated_at TIMESTAMP WITHOUT TIME ZONE,

   CONSTRAINT pk_category PRIMARY KEY (id)
);

DROP TABLE IF NOT EXISTS proposal;

CREATE TABLE proposal (
    id uuid NOT NULL, -- PII protection, on chain we are not allowed to store human readable names
    name VARCHAR(256) NOT NULL, -- PII protection, on chain we are not allowed to store human readable names
    category_id VARCHAR(256) NOT NULL REFERENCES category(id),

    absolute_slot BIGINT NOT NULL,

    created_at TIMESTAMP WITHOUT TIME ZONE,
    updated_at TIMESTAMP WITHOUT TIME ZONE,

    CONSTRAINT pk_proposal PRIMARY KEY (id)
);

DROP TABLE IF NOT EXISTS merkle_root_hash;

CREATE TABLE merkle_root_hash (
    root_hash VARCHAR(256) NOT NULL,  -- merkle root hash
    event_id VARCHAR(256) NOT NULL, -- human readable name, should never contain PII data

    absolute_slot BIGINT NOT NULL,

    created_at TIMESTAMP WITHOUT TIME ZONE,
    updated_at TIMESTAMP WITHOUT TIME ZONE,

    CONSTRAINT pk_merkle_root_hash PRIMARY KEY (id)
);

CREATE INDEX idx_merkle_root_hash_rollback
    ON vote_merkle_proof (absolute_slot);
