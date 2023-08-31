import { VoteReceipt } from '../types/voting-app-types';
import { EventPresentation } from '../types/voting-ledger-follower-types';

export interface UserState {
  connectedWallet: string;
  walletIsVerified: boolean;
  isReceiptFetched: boolean;
  receipt: VoteReceipt | null;
  proposal: VoteReceipt['proposal'];
  event?: EventPresentation;
}

export interface State {
  user: UserState;
}
