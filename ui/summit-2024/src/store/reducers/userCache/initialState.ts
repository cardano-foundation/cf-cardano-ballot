import {
  ConnectedWallet,
  UserCacheProps,
  UserVotes,
  VerificationStarted,
} from "./userCache.types";

const initialVerificationStarted: VerificationStarted = {
  eventId: "",
  stakeAddress: "",
  requestId: "",
  createdAt: "",
  expiresAt: "",
};

const initialConnectedWallet: ConnectedWallet = {
  address: "",
  name: "",
  icon: "",
  requestAutoconnect: false,
  version: "",
};

const initialUserVotes: UserVotes[] = [];

const initialStateData: UserCacheProps = {
  walletIdentifier: "",
  connectedWallet: initialConnectedWallet,
  verificationStarted: initialVerificationStarted,
  userVotes: initialUserVotes,
  isVerified: false,
};

export { initialStateData };
