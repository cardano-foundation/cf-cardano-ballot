import {IWalletInfo} from "../../../components/ConnectWalletList/ConnectWalletList.types";

interface VerificationStarted {
  eventId: string;
  stakeAddress: string;
  requestId: string;
  createdAt: string;
  expiresAt: string;
}

interface UserVotes {
  categoryId: string;
  proposalId: string;
}

interface UserCacheProps {
  walletIdentifier: string;
  connectedWallet: IWalletInfo;
  verificationStarted: VerificationStarted;
  userVotes: UserVotes[];
  isVerified: boolean;
}

export type { VerificationStarted, UserVotes, UserCacheProps };
