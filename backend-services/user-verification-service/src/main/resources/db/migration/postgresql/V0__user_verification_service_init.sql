DROP TABLE IF EXISTS user;

CREATE TABLE user (
   voter_stake_address VARCHAR(255) NOT NULL,
   is_verified BOOL NOT NULL,

   created_at TIMESTAMP WITHOUT TIME ZONE,
   updated_at TIMESTAMP WITHOUT TIME ZONE,

   CONSTRAINT pk_user PRIMARY KEY (voter_stake_address)
);
