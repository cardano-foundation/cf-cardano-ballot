DROP TABLE IF EXISTS event;

CREATE TABLE event (
    id VARCHAR(255) NOT NULL, -- human readable name, should never contain PII data
    team VARCHAR(255) NOT NULL,
    schema_version VARCHAR(255) NOT NULL,
    event_type VARCHAR(255) NOT NULL,
    allow_vote_changing BOOL,
    category_results_while_voting BOOL,
    high_level_results_while_voting BOOL,

    voting_power_asset VARCHAR(255),

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
    name VARCHAR(255) NOT NULL, -- PII protection, on chain we are not allowed to store human readable names
    category_id VARCHAR(255) NOT NULL,

    absolute_slot BIGINT NOT NULL,

    created_at TIMESTAMP WITHOUT TIME ZONE,
    updated_at TIMESTAMP WITHOUT TIME ZONE,

   CONSTRAINT pk_proposal PRIMARY KEY (id),
   CONSTRAINT fk_proposal_category_id FOREIGN KEY (category_id) REFERENCES category(id)
);

DROP TABLE IF EXISTS vote;

CREATE TABLE vote (
   id VARCHAR(255) NOT NULL,
   event_id VARCHAR(255) NOT NULL,
   category_id VARCHAR(255) NOT NULL,
   proposal_id VARCHAR(255) NOT NULL,
   voter_stake_address VARCHAR(255) NOT NULL,
   cose_signature TEXT NOT NULL,
   cose_public_key VARCHAR(255),
   voting_power BIGINT,
   voted_at_slot BIGINT NOT NULL,

   created_at TIMESTAMP WITHOUT TIME ZONE,
   updated_at TIMESTAMP WITHOUT TIME ZONE,

   CONSTRAINT pk_vote PRIMARY KEY (id)
);

CREATE INDEX idx_vote_stake_key
    ON vote (event_id, category_id, voter_stake_address);

CREATE INDEX idx_vote_event_id
    ON vote (event_id);

CREATE INDEX idx_vote_event_id_category_id_proposal_id
    ON vote (event_id, category_id, proposal_id);

DROP TABLE IF EXISTS vote_merkle_proof;

-- benefit of storing vote merkle proof is that upon restart of app voter's receipt can be served from local db
CREATE TABLE vote_merkle_proof (
   vote_id VARCHAR(255) NOT NULL,
   event_id VARCHAR(255) NOT NULL,
   root_hash VARCHAR(255) NOT NULL, -- merkle root hash as hex string
   l1_transaction_hash VARCHAR(255) NOT NULL, -- transaction hash as hex string
   proof_items_json TEXT NOT NULL, -- json blob representing actual merkle proof
   absolute_slot BIGINT NOT NULL,
   invalidated BOOL NOT NULL,

   created_at TIMESTAMP WITHOUT TIME ZONE,
   updated_at TIMESTAMP WITHOUT TIME ZONE,

   CONSTRAINT pk_vote_merkle_proof PRIMARY KEY (vote_id)
);

-- special index to find out all vote_merkle_proofs that took part in a given event
CREATE INDEX idx_vote_merkle_proof_vote_id_event_id
    ON vote_merkle_proof (vote_id, event_id);

-- special index to help us find out all vote_merkle_proofs that took part in rolled back transaction
CREATE INDEX idx_vote_merkle_proof_transaction_rollback
    ON vote_merkle_proof (absolute_slot);
