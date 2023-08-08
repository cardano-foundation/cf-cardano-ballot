import { VoteReceipt, EventReference } from 'types/backend-services-types';

export interface UserState {
  isConnectWalletModalVisible: boolean;
  isVoteSubmittedModalVisible: boolean;
  isVerifyVoteModalVisible: boolean;
  connectedWallet: string;
  isReceiptFetched: boolean;
  receipt: VoteReceipt | null;
  proposal: VoteReceipt['proposal'];
  event: EventReference;
}

export interface State {
  user: UserState;
}
