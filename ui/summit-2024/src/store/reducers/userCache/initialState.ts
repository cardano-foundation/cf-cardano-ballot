import {
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

const initialUserVotes: UserVotes[] = [];

const initialStateData: UserCacheProps = {
  walletIdentifier: "",
  verificationStarted: initialVerificationStarted,
  userVotes: initialUserVotes,
  isVerified: false,
};

export { initialStateData };
