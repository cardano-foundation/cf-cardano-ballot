export const env = {
  // Services URLs
  APP_SERVER_URL: process.env.REACT_APP_SERVER_URL,
  // config vars
  TARGET_NETWORK: process.env.REACT_APP_TARGET_NETWORK,
  EVENT_ID: process.env.REACT_APP_EVENT_ID,
  CATEGORY_ID: process.env.REACT_APP_CATEGORY_ID,
  COMMIT_HASH: process.env.REACT_APP_COMMIT_HASH,
  SUPPORTED_WALLETS: (process.env.REACT_APP_SUPPORTED_WALLETS || '').split(',').filter((w) => !!w),
};
