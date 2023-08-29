DROP TABLE IF EXISTS user_verification;

CREATE TABLE user_verification (
   stake_address VARCHAR(255) NOT NULL,
   event_id VARCHAR(255) NOT NULL,

   request_id VARCHAR(255) NOT NULL,
   phone_number_hash VARCHAR(255) NOT NULL,
   verification_code VARCHAR(255) NOT NULL,

   status VARCHAR(255) NOT NULL,
   provider VARCHAR(255) NOT NULL,
   channel VARCHAR(255) NOT NULL,

   created_at TIMESTAMP WITHOUT TIME ZONE,
   updated_at TIMESTAMP WITHOUT TIME ZONE,

   CONSTRAINT pk_user PRIMARY KEY (stake_address)
);

create index idx_stake_address_status on user_verification(event_id, stake_address, status);

create index idx_status on user_verification(event_id, status);