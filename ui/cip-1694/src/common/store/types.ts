import { VoteReceipt } from 'types/voting-app-types';
import { ChainTip, EventPresentation } from 'types/voting-ledger-follower-types';

export interface UserState {
  isConnectWalletModalVisible: boolean;
  isVoteSubmittedModalVisible: boolean;
  connectedWallet: string;
  isReceiptFetched: boolean;
  receipt: VoteReceipt | null;
  proposal: VoteReceipt['proposal'];
  event: EventPresentation;
  tip: ChainTip;
}

export interface State {
  user: UserState;
}
