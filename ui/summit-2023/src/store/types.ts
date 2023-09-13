import { VoteReceipt } from '../types/voting-app-types';
import { EventPresentation } from '../types/voting-ledger-follower-types';

export interface UserState {
  connectedWallet: string;
  walletIsVerified: boolean;
  walletIsLoggedIn: boolean;
  isReceiptFetched: boolean;
  receipts: { [categoryId: string]: VoteReceipt } | null;
  proposal: VoteReceipt['proposal'];
  event?: EventPresentation;
  userVerification?: {
    [stakeAddress: string]: VerificationStarts;
  };
}

export interface VerificationStarts {
  eventId: string;
  stakeAddress: string;
  requestId: string;
  createdAt: string;
  expiresAt: string;
}

export interface PhoneNumberCodeConfirmation {
  verified: boolean;
}

export interface State {
  user: UserState;
}
