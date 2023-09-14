DROP TABLE IF EXISTS sms_user_verification;

CREATE TABLE sms_user_verification (
   id VARCHAR(255) NOT NULL,
   stake_address VARCHAR(255) NOT NULL,
   event_id VARCHAR(255) NOT NULL,

   request_id VARCHAR(255) NOT NULL,
   phone_number_hash VARCHAR(255) NOT NULL,
   verification_code VARCHAR(255) NOT NULL,

   status VARCHAR(255) NOT NULL,

   created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
   updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,

   expires_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,

   CONSTRAINT pk_user PRIMARY KEY (id)
);

CREATE TABLE discord_user_verification (
   hashed_discord_id VARCHAR(255) NOT NULL,

   stake_address VARCHAR(255) NOT NULL,
   event_id VARCHAR(255) NOT NULL,

   verification_code VARCHAR(255) NOT NULL,

   status VARCHAR(255) NOT NULL,

   created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
   updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,

   expires_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,

   CONSTRAINT pk_user PRIMARY KEY (id)
);

create index idx_sms_event on sms_user_verification(event_id);

create index idx_sms_status on sms_user_verification(event_id, status);

create index idx_sms_stake_address_status on sms_user_verification(event_id, stake_address, status);

create index idx_sms_status_phone_hash on sms_user_verification(event_id, status, phone_number_hash);

create index idx_sms_stake_address_status_phone_hash on sms_user_verification(event_id, stake_address, status, phone_number_hash);

create index idx_sms_stake_address_status_req_id on sms_user_verification(event_id, stake_address, status, request_id);
