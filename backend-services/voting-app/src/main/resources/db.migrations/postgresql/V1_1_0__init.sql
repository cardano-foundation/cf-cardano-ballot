DROP TABLE IF NOT EXISTS event;

CREATE TABLE event (
    id VARCHAR(255) NOT NULL, -- human readable name, should never contain PII data
    team VARCHAR(255) NOT NULL,
    schema_version VARCHAR(255) NOT NULL,
    event_type INT NOT NULL,
    allow_vote_changing BOOL, -- TODO default false
    category_results_while_voting BOOL, -- TODO default false

    voting_power_asset INT,

    start_epoch INT,
    end_epoch INT,

    start_slot INT,
    end_slot INT,

    snapshot_epoch INT,

    created_at TIMESTAMP WITHOUT TIME ZONE,
    updated_at TIMESTAMP WITHOUT TIME ZONE,

   CONSTRAINT pk_event PRIMARY KEY (id)
);

DROP TABLE IF NOT EXISTS category;

CREATE TABLE category (
    id VARCHAR(255) NOT NULL, -- human readable name, should never contain PII data
    event_id VARCHAR(255) NOT NULL REFERENCES event(id),
    schema_version VARCHAR(255) NOT NULL,
    gdpr_protection BOOL NOT NULL,

    created_at TIMESTAMP WITHOUT TIME ZONE,
    updated_at TIMESTAMP WITHOUT TIME ZONE,

   CONSTRAINT pk_category PRIMARY KEY (id)
);

DROP TABLE IF NOT EXISTS proposal;

CREATE TABLE proposal (
    id uuid NOT NULL, -- PII protection, on chain we are not allowed to store human readable names
    name VARCHAR(255 NOT NULL, -- PII protection, on chain we are not allowed to store human readable names
    category_id VARCHAR(255) NOT NULL REFERENCES category(id),

    created_at TIMESTAMP WITHOUT TIME ZONE,
    updated_at TIMESTAMP WITHOUT TIME ZONE,

   CONSTRAINT pk_proposal PRIMARY KEY (id)
);

DROP TABLE IF NOT EXISTS vote;

CREATE TABLE vote (
   id uuid NOT NULL,
   event_id VARCHAR(255) NOT NULL,
   category_id VARCHAR(255) NOT NULL,
   proposal_id VARCHAR(255) NOT NULL,
   voter_staking_address VARCHAR(255) NOT NULL,
   cose_signature text NOT NULL,
   cose_public_key VARCHAR(255),
   voting_power BIGINT,
   voted_at_slot BIGINT NOT NULL,

   created_at TIMESTAMP WITHOUT TIME ZONE,
   updated_at TIMESTAMP WITHOUT TIME ZONE,

   CONSTRAINT pk_vote PRIMARY KEY (vote)
);

CREATE INDEX idx_vote_stake_key
    ON vote (event_id, category_id, voter_staking_address);

DROP TABLE IF NOT EXISTS vote_merkle_proof;

-- benefit of storing vote merkle proof is that upon restart of app voter's receipt can be served from local db
CREATE TABLE vote_merkle_proof (
   vote_id uuid NOT NULL,
   event_id VARCHAR(255) NOT NULL,
   root_hash VARCHAR(255) NOT NULL, -- merkle root hash as hex string
   l1_transaction_hash VARCHAR(255) NOT NULL, -- transaction hash as hex string
   proof_items_json json NOT NULL, -- json representing actual merkle proof
   absolute_slot BIGINT,
   block_hash VARCHAR(255), -- block hash as hex string
   invalidated BOOL NOT NULL,

   created_at TIMESTAMP WITHOUT TIME ZONE,
   updated_at TIMESTAMP WITHOUT TIME ZONE,

   CONSTRAINT pk_vote PRIMARY KEY (vote_id)
);

-- special index to find out all vote_merkle_proofs that took part in a given event
CREATE INDEX idx_vote_merkle_proof_vote_id_event_id
    ON vote_merkle_proof (vote_id, event_d);

-- special index to help us find out all vote_merkle_proofs that took part in rolled back transaction
CREATE INDEX idx_vote_merkle_proof_transaction_rollback
    ON vote_merkle_proof (absolute_slot);
