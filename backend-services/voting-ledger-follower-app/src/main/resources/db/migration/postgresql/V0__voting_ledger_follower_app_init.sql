DROP TABLE IF EXISTS event;

CREATE TABLE event (
    id VARCHAR(255) NOT NULL, -- human readable name, should never contain PII data
    organisers VARCHAR(255) NOT NULL,
    schema_version VARCHAR(255) NOT NULL,
    event_type VARCHAR(255) NOT NULL,
    allow_vote_changing BOOL,

    high_level_event_results_while_voting BOOL,
    high_level_category_results_while_voting BOOL,
    category_results_while_voting BOOL,

    voting_power_asset VARCHAR(255),

    start_epoch INT,
    end_epoch INT,
    proposals_reveal_epoch INT,
    snapshot_epoch INT,

    start_slot BIGINT,
    end_slot BIGINT,
    proposals_reveal_slot BIGINT,

    absolute_slot BIGINT NOT NULL,

    created_at TIMESTAMP WITHOUT TIME ZONE,
    updated_at TIMESTAMP WITHOUT TIME ZONE,

   CONSTRAINT pk_event PRIMARY KEY (id)
);

DROP TABLE IF EXISTS event_tally;

CREATE TABLE event_tally (
    name VARCHAR(255) NOT NULL, -- human readable name, should never contain PII data
    event_id VARCHAR(255) NOT NULL,
    description VARCHAR(255),
    type VARCHAR(255) NOT NULL,

    hydra_tally_config__contract_name VARCHAR(255) NOT NULL,
    hydra_tally_config__contract_description VARCHAR(255),
    hydra_tally_config__contract_version VARCHAR(255) NOT NULL,
    hydra_tally_config__compiled_script TEXT NOT NULL,
    hydra_tally_config__compiled_script_hash VARCHAR(255) NOT NULL,
    hydra_tally_config__compiler_name VARCHAR(255) NOT NULL,
    hydra_tally_config__compiler_version VARCHAR(255) NOT NULL,
    hydra_tally_config__plutus_version VARCHAR(255) NOT NULL,
    hydra_tally_config__verification_key_hashes TEXT NOT NULL,

    created_at TIMESTAMP WITHOUT TIME ZONE,
    updated_at TIMESTAMP WITHOUT TIME ZONE,

   CONSTRAINT pk_event_tally PRIMARY KEY (name)
);

DROP TABLE IF EXISTS category;

CREATE TABLE category (
    id VARCHAR(255) NOT NULL, -- human readable name, should never contain PII data
    event_id VARCHAR(255) NOT NULL,
    schema_version VARCHAR(255) NOT NULL,
    gdpr_protection BOOL NOT NULL,

    absolute_slot BIGINT NOT NULL,

    created_at TIMESTAMP WITHOUT TIME ZONE,
    updated_at TIMESTAMP WITHOUT TIME ZONE,

   CONSTRAINT pk_category PRIMARY KEY (id),
   CONSTRAINT fk_category_event_id FOREIGN KEY (event_id) REFERENCES event(id)
);

DROP TABLE IF EXISTS proposal;

CREATE TABLE proposal (
    id VARCHAR(255) NOT NULL, -- PII protection, on chain we are not allowed to store human readable names
    name VARCHAR(255), -- PII protection, on chain we are not allowed to store human readable names
    category_id VARCHAR(255) NOT NULL,

    absolute_slot BIGINT NOT NULL,

    created_at TIMESTAMP WITHOUT TIME ZONE,
    updated_at TIMESTAMP WITHOUT TIME ZONE,

   CONSTRAINT pk_proposal PRIMARY KEY (id),
   CONSTRAINT fk_proposal_category_id FOREIGN KEY (category_id) REFERENCES category(id)
);

DROP TABLE IF EXISTS merkle_root_hash;

CREATE TABLE merkle_root_hash (
    id VARCHAR(256) NOT NULL,  -- merkle root hash
    event_id VARCHAR(256) NOT NULL, -- human readable name, should never contain PII data

    absolute_slot BIGINT NOT NULL,

    created_at TIMESTAMP WITHOUT TIME ZONE,
    updated_at TIMESTAMP WITHOUT TIME ZONE,

    CONSTRAINT pk_merkle_root_hash PRIMARY KEY (id)
);

CREATE INDEX idx_merkle_root_hash_event_id_and_id
    ON merkle_root_hash(id, event_id);

CREATE INDEX idx_merkle_root_hash_rollback
    ON merkle_root_hash(absolute_slot);

DROP TABLE IF EXISTS utxo_category_result;

CREATE TABLE event_category_result_utxo_data (
    id VARCHAR(255) NOT NULL,
    address VARCHAR(255) NOT NULL,
    tx_hash VARCHAR(255) NOT NULL,
    index INT NOT NULL,
    inline_datum TEXT NOT NULL,
    absolute_slot BIGINT NOT NULL,
    witnesses_hashes TEXT NOT NULL,

    CONSTRAINT pk_event_category_result_utxo_data PRIMARY KEY (id)
);

CREATE INDEX idx_utxo_category_result_address
    ON event_category_result_utxo_data(address);

CREATE INDEX idx_utxo_category_result_rollback
    ON event_category_result_utxo_data(absolute_slot);