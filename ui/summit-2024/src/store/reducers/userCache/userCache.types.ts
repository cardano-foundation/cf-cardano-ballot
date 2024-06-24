import { IWalletInfo } from "../../../components/ConnectWalletList/ConnectWalletList.types";

interface ConnectedWallet {
  address: string;
  name: string;
  icon: string;
  requestAutoconnect: boolean;
  version: string;
}

interface VerificationStarted {
  eventId: string;
  stakeAddress?: string;
  requestId: string;
  createdAt: string;
  expiresAt: string;
}
interface VerificationStartedExtended extends VerificationStarted {
  walletIdentifier?: string;
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

export type {
  VerificationStarted,
  VerificationStartedExtended,
  UserVotes,
  UserCacheProps,
  ConnectedWallet
};
