DROP TABLE IF EXISTS user_verification;

CREATE TABLE user_verification (
   stake_address VARCHAR(255) NOT NULL,
   event_id VARCHAR(255) NOT NULL,

   phone_number VARCHAR(255), -- we store it temporarily and remove phone number upon successful or unsuccessful verification

   status VARCHAR(255) NOT NULL,
   provider VARCHAR(255) NOT NULL,
   channel VARCHAR(255) NOT NULL,

   created_at TIMESTAMP WITHOUT TIME ZONE,
   updated_at TIMESTAMP WITHOUT TIME ZONE,

   CONSTRAINT pk_user PRIMARY KEY (voter_stake_address)
);

create index idx_status on user_verification(status);