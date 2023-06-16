DROP TABLE IF NOT EXISTS event;

CREATE TABLE event (
    id uuid NOT NULL,
    name VARCHAR(255) NOT NULL, -- human readable name
    team VARCHAR(255) NOT NULL,
    presentation_name VARCHAR(255) NOT NULL,
    description VARCHAR(255),
    start_epoch INT NOT NULL,
    end_epoch INT NOT NULL,
    snapshot_epoch INT NOT NULL,

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

DROP TABLE IF NOT EXISTS root_hash;

CREATE TABLE root_hash (
    event_id uuid NOT NULL,
    root_hash VARCHAR(255) NOT NULL,

   CONSTRAINT pk_root_hash PRIMARY KEY (event_id)
);

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