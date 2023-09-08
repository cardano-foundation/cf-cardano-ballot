// Services URLs
const VOTING_APP_SERVER_URL = process.env.REACT_APP_VOTING_APP_SERVER_URL;
const VOTING_LEDGER_FOLLOWER_APP_SERVER_URL = process.env.REACT_APP_VOTING_LEDGER_FOLLOWER_APP_SERVER_URL;
const VOTING_VERIFICATION_APP_SERVER_URL = process.env.REACT_APP_VOTING_VERIFICATION_APP_SERVER_URL;
const VOTING_USER_VERIFICATION_SERVER_URL = process.env.REACT_APP_USER_VERIFICATION_SERVER_URL;
// config vars
const TARGET_NETWORK = process.env.REACT_APP_TARGET_NETWORK;
const EVENT_ID = process.env.REACT_APP_EVENT_ID;
const CATEGORY_ID = process.env.REACT_APP_CATEGORY_ID;
const COMMIT_HASH = process.env.REACT_APP_COMMIT_HASH;
const SUPPORTED_WALLETS = (process.env.REACT_APP_SUPPORTED_WALLETS || '').split(',').filter((w) => !!w);

export const env = {
  VOTING_APP_SERVER_URL,
  VOTING_LEDGER_FOLLOWER_APP_SERVER_URL,
  VOTING_VERIFICATION_APP_SERVER_URL,
  VOTING_USER_VERIFICATION_SERVER_URL,
  TARGET_NETWORK,
  EVENT_ID,
  CATEGORY_ID,
  COMMIT_HASH,
  SUPPORTED_WALLETS,
};
