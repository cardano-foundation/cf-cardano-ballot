import { get } from 'lodash';

// Services URLs
const VOTING_APP_SERVER_URL =
  process.env.REACT_APP_VOTING_APP_SERVER_URL || get(window, 'env.REACT_APP_VOTING_APP_SERVER_URL');
const VOTING_LEDGER_FOLLOWER_APP_SERVER_URL =
  process.env.REACT_APP_VOTING_LEDGER_FOLLOWER_APP_SERVER_URL ||
  get(window, 'env.REACT_APP_VOTING_LEDGER_FOLLOWER_APP_SERVER_URL');
const VOTING_VERIFICATION_APP_SERVER_URL =
  process.env.REACT_APP_VOTING_VERIFICATION_APP_SERVER_URL ||
  get(window, 'env.REACT_APP_VOTING_VERIFICATION_APP_SERVER_URL');
const VOTING_USER_VERIFICATION_SERVER_URL =
  process.env.REACT_APP_USER_VERIFICATION_SERVER_URL || get(window, 'env.REACT_APP_USER_VERIFICATION_SERVER_URL');
const WEB_URL = process.env.REACT_APP_WEB_URL || get(window, 'env.REACT_APP_WEB_URL');
const DISCORD_CHANNEL_URL =
  process.env.REACT_APP_DISCORD_CHANNEL_URL || get(window, 'env.REACT_APP_DISCORD_CHANNEL_URL');
const REACT_APP_DISCORD_BOT_URL = process.env.REACT_APP_DISCORD_BOT_URL || get(window, 'env.REACT_APP_DISCORD_BOT_URL');
// config vars
const TARGET_NETWORK = process.env.REACT_APP_TARGET_NETWORK || get(window, 'env.REACT_APP_TARGET_NETWORK');
const EVENT_ID = process.env.REACT_APP_EVENT_ID || get(window, 'env.REACT_APP_EVENT_ID');
const APP_VERSION = process.env.REACT_APP_VERSION || get(window, 'env.REACT_APP_VERSION');
const SUPPORTED_WALLETS = (process.env.REACT_APP_SUPPORTED_WALLETS || get(window, 'env.REACT_APP_SUPPORTED_WALLETS'))
  .split(',')
  .filter((w) => !!w);

export const env = {
  VOTING_APP_SERVER_URL,
  VOTING_LEDGER_FOLLOWER_APP_SERVER_URL,
  VOTING_VERIFICATION_APP_SERVER_URL,
  VOTING_USER_VERIFICATION_SERVER_URL,
  TARGET_NETWORK,
  EVENT_ID,
  APP_VERSION,
  SUPPORTED_WALLETS,
  WEB_URL,
  DISCORD_CHANNEL_URL,
  REACT_APP_DISCORD_BOT_URL,
};
