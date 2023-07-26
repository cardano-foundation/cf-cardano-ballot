// Services URLs
export const EVENT_BY_ID_REFERENCE_URL = process.env.EVENT_BY_ID_REFERENCE_URL;
export const CAST_VOTE_URL = process.env.REACT_APP_CAST_VOTE_URL;
export const VOTE_RECEIPT_URL = process.env.REACT_APP_VOTE_RECEIPT_URL;
export const BLOCKCHAIN_TIP_URL = process.env.REACT_APP_BLOCKCHAIN_TIP_URL;
export const VOTING_POWER_URL = process.env.REACT_APP_VOTING_POWER_URL;

// config vars
export const TARGET_NETWORK = process.env.REACT_APP_TARGET_NETWORK;
export const EVENT_ID = process.env.REACT_APP_EVENT_ID;
export const CATEGORY_ID = process.env.REACT_APP_CATEGORY_ID;
export const COMMIT_HASH = process.env.REACT_APP_COMMIT_HASH;
export const SUPPORTED_WALLETS = (process.env.REACT_APP_SUPPORTED_WALLETS || '').split(',').filter((w) => !!w);
export const ALWAYS_VISIBLE_WALLETS = (process.env.REACT_APP_ALWAYS_VISIBLE_WALLETS || '')
  .split(',')
  .filter((w) => !!w);
export const EVENT_END_TIME = process.env.REACT_APP_EVENT_END_TIME; // summit date
export const EVENT_END_TIME_FORMAT = process.env.REACT_APP_EVENT_END_TIME_FORMAT;
