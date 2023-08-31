DROP TABLE IF EXISTS user_verification;

CREATE TABLE user_verification (
   id VARCHAR(255) NOT NULL,
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

   CONSTRAINT pk_user PRIMARY KEY (id)
);

create index idx_status on user_verification(event_id, status);

create index idx_stake_address_status on user_verification(event_id, stake_address, status);

create index idx_stake_address_status_phone_hash on user_verification(event_id, stake_address, status, phone_number_hash);

create index idx_stake_address_status_req_id on user_verification(event_id, stake_address, status, request_id);
