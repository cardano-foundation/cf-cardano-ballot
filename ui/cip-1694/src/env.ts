import get from 'lodash/get';

declare global {
  interface Window {
    // eslint-disable-next-line @typescript-eslint/no-explicit-any
    env: any;
  }
}

// change with your own variables
type EnvType = {
  VOTING_APP_SERVER_URL: string;
  VOTING_LEDGER_FOLLOWER_APP_SERVER_URL: string;
  VOTING_VERIFICATION_APP_SERVER_URL: string;
  GOOGLE_FORM_URL: string;
  TARGET_NETWORK: string;
  EVENT_ID: string;
  CATEGORY_ID: string;
  COMMIT_HASH: string;
  SUPPORTED_WALLETS: string[];
  QUESTIONS: string[];
  DISCORD_URL: string;
  STATUS_PAGE_URL: string;
  FAQ_URL: string;
  ENV_URI: string;
};

export const env: EnvType = {
  // Services URLs
  VOTING_APP_SERVER_URL:
    process.env.REACT_APP_VOTING_APP_SERVER_URL || get(window, 'env.REACT_APP_VOTING_APP_SERVER_URL'),
  VOTING_LEDGER_FOLLOWER_APP_SERVER_URL:
    process.env.REACT_APP_VOTING_LEDGER_FOLLOWER_APP_SERVER_URL ||
    get(window, 'env.REACT_APP_VOTING_LEDGER_FOLLOWER_APP_SERVER_URL'),
  VOTING_VERIFICATION_APP_SERVER_URL:
    process.env.REACT_APP_VOTING_VERIFICATION_APP_SERVER_URL ||
    get(window, 'env.REACT_APP_VOTING_VERIFICATION_APP_SERVER_URL'),
  GOOGLE_FORM_URL: process.env.REACT_APP_GOOGLE_FORM_URL || get(window, 'env.REACT_APP_GOOGLE_FORM_URL'),
  // config vars
  TARGET_NETWORK: process.env.REACT_APP_TARGET_NETWORK || get(window, 'env.REACT_APP_TARGET_NETWORK'),
  EVENT_ID: process.env.REACT_APP_EVENT_ID || get(window, 'env.REACT_APP_EVENT_ID'),
  CATEGORY_ID: process.env.REACT_APP_CATEGORY_ID || get(window, 'env.REACT_APP_CATEGORY_ID'),
  COMMIT_HASH: process.env.REACT_APP_COMMIT_HASH || get(window, 'env.REACT_APP_COMMIT_HASH'),
  SUPPORTED_WALLETS: (process.env.REACT_APP_SUPPORTED_WALLETS || get(window, 'env.REACT_APP_SUPPORTED_WALLETS') || '')
    .split(',')
    .filter((w: string) => !!w),
  QUESTIONS: (process.env.REACT_APP_QUESTIONS || get(window, 'env.REACT_APP_QUESTIONS') || '')
    .split(';')
    .filter((q: string) => !!q),
  DISCORD_URL: process.env.REACT_APP_DISCORD_URL || get(window, 'env.REACT_APP_DISCORD_URL'),
  STATUS_PAGE_URL: process.env.REACT_APP_STATUS_PAGE_URL || get(window, 'env.REACT_APP_STATUS_PAGE_URL'),
  FAQ_URL: process.env.REACT_APP_FAQ_URL || get(window, 'env.REACT_APP_FAQ_URL'),
  ENV_URI: process.env.REACT_APP_ENV_URI || get(window, 'env.REACT_APP_ENV_URI'),
};
