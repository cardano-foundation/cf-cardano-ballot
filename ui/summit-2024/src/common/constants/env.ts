const VOTING_APP_SERVER_URL = import.meta.env.VITE_VOTING_APP_SERVER_URL;
const VOTING_LEDGER_FOLLOWER_APP_SERVER_URL = import.meta.env
  .VITE_VOTING_LEDGER_FOLLOWER_APP_SERVER_URL;
const VOTING_VERIFICATION_APP_SERVER_URL = import.meta.env
  .VITE_VOTING_VERIFICATION_APP_SERVER_URL;
const VOTING_USER_VERIFICATION_SERVER_URL = import.meta.env
  .VITE_USER_VERIFICATION_SERVER_URL;
const FRONTEND_URL = import.meta.env.VITE_WEB_URL;
const MATOMO_BASE_URL = import.meta.env.VITE_MATOMO_BASE_URL || "none";
const DISCORD_CHANNEL_URL = import.meta.env.VITE_DISCORD_CHANNEL_URL;
const COMMIT_HASH = import.meta.env.VITE_COMMIT_HASH;
const DISCORD_BOT_URL = import.meta.env.VITE_DISCORD_BOT_URL;
const DISCORD_SUPPORT_CHANNEL_URL = import.meta.env
  .VITE_DISCORD_SUPPORT_CHANNEL_URL;
const TARGET_NETWORK = import.meta.env.VITE_TARGET_NETWORK;
const USING_FIXTURES = import.meta.env.VITE_USING_FIXTURES === "true";
const EVENT_ID = import.meta.env.VITE_EVENT_ID;
const APP_VERSION = import.meta.env.VITE_VERSION;
const SUPPORTED_WALLETS = import.meta.env.VITE_SUPPORTED_WALLETS?.split(
  ",",
).filter(Boolean);
const SHOW_WINNERS = import.meta.env.VITE_SHOW_WINNERS === "true";
const SHOW_HYDRA_TALLY = import.meta.env.VITE_SHOW_HYDRA_TALLY === "true";

export const env = {
  VOTING_APP_SERVER_URL,
  VOTING_LEDGER_FOLLOWER_APP_SERVER_URL,
  VOTING_VERIFICATION_APP_SERVER_URL,
  VOTING_USER_VERIFICATION_SERVER_URL,
  TARGET_NETWORK,
  USING_FIXTURES,
  EVENT_ID,
  APP_VERSION,
  SUPPORTED_WALLETS,
  MATOMO_BASE_URL,
  FRONTEND_URL,
  COMMIT_HASH,
  DISCORD_CHANNEL_URL,
  DISCORD_BOT_URL,
  DISCORD_SUPPORT_CHANNEL_URL,
  SHOW_WINNERS,
  SHOW_HYDRA_TALLY,
};
