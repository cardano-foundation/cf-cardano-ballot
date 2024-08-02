import get from 'lodash/get';

export const env = {
  // Services URLs
  VOTING_VERIFICATION_APP_SERVER_URL:
    process.env.REACT_APP_VOTING_VERIFICATION_APP_SERVER_URL ||
    get(window, 'env.REACT_APP_VOTING_VERIFICATION_APP_SERVER_URL'),
  // config vars
  TARGET_NETWORK: process.env.REACT_APP_TARGET_NETWORK || get(window, 'env.REACT_APP_TARGET_NETWORK'),
};
