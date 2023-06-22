DROP TABLE IF NOT EXISTS event;

CREATE TABLE event (
    id uuid NOT NULL,
    name VARCHAR(255) NOT NULL, -- human readable name
    team VARCHAR(255) NOT NULL,
    presentation_name VARCHAR(255),
    description VARCHAR(255),
    event_type INT NOT NULL,

    start_epoch INT,
    end_epoch INT,

    start_slot INT,
    end_slot INT,

    snapshot_epoch INT,

    create_datetime TIMESTAMP WITHOUT TIME ZONE,
    update_datetime TIMESTAMP WITHOUT TIME ZONE,

   CONSTRAINT pk_event PRIMARY KEY (id)
);

CREATE INDEX idx_event_name
    ON event (name);

DROP TABLE IF NOT EXISTS category;

CREATE TABLE category (
    id uuid NOT NULL,
    name VARCHAR(255) NOT NULL, -- human readable name
    presentation_name VARCHAR(255) NOT NULL,
    description VARCHAR(255),
    event_id uuid NOT NULL REFERENCES event(id),

    create_datetime TIMESTAMP WITHOUT TIME ZONE,
    update_datetime TIMESTAMP WITHOUT TIME ZONE,

   CONSTRAINT pk_category PRIMARY KEY (id)
);

CREATE INDEX idx_category_name
    ON category (name);

DROP TABLE IF NOT EXISTS proposal;

CREATE TABLE proposal (
    id uuid NOT NULL,
    name VARCHAR(255) NOT NULL, -- human readable name
    presentation_name VARCHAR(255) NOT NULL,
    description VARCHAR(255),
    category_id uuid NOT NULL REFERENCES category(id),

    create_datetime TIMESTAMP WITHOUT TIME ZONE,
    update_datetime TIMESTAMP WITHOUT TIME ZONE,

   CONSTRAINT pk_proposal PRIMARY KEY (id)
);

CREATE INDEX idx_proposal_name
    ON proposal (name);

DROP TABLE IF NOT EXISTS vote;

CREATE TABLE vote (
   id uuid NOT NULL,
   event_id VARCHAR(255) NOT NULL,
   category_id VARCHAR(255) NOT NULL,
   proposal_id VARCHAR(255) NOT NULL,
   voter_staking_address VARCHAR(255) NOT NULL,
   cose_signature text NOT NULL,
   cose_public_key text NOT NULL,
   voting_power BIGINT NOT NULL,
   network INT NOT NULL,
   voted_at_slot BIGINT NOT NULL,

   CONSTRAINT pk_vote PRIMARY KEY (vote)
);

CREATE INDEX idx_vote_stake_key
    ON vote (event_id, category_id, voter_staking_address);

DROP TABLE IF NOT EXISTS vote_merkle_proof;

CREATE TABLE vote_merkle_proof (
   vote_id uuid NOT NULL,
   root_hash VARCHAR(255) NOT NULL, -- merkle root hash as hex string
   l1_transaction_hash VARCHAR(255) NOT NULL, -- transaction hash as hex string
   absolute_slot BIGINT NOT NULL, -- absolute slot number
   block_hash VARCHAR(255) NOT NULL, -- block hash as hex string
   proof_items_json json NOT NULL, -- json representing actual merkle proof

   CONSTRAINT pk_vote PRIMARY KEY (vote_id)
);

-- special index to help us find out all vote_merkle_proofs that took part in rolled back transaction
CREATE INDEX idx_vote_merkle_proof_transaction_rollback
    ON vote_merkle_proof (absolute_slot, block_hash);
