DROP TABLE IF EXISTS vote;

CREATE TABLE vote (
   id VARCHAR(255) NOT NULL,
   id_numeric_hash BIGINT NOT NULL,
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
   vote_id_numeric_hash BIGINT NOT NULL,
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
