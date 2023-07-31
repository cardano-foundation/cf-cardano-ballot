import { VoteReceipt } from 'types/backend-services-types';

export interface UserState {
  isConnectWalletModalVisible: boolean;
  isVoteSubmittedModalVisible: boolean;
  isVerifyVoteModalVisible: boolean;
  connectedWallet: string;
  isReceiptFetched: boolean;
  receipt: VoteReceipt | null;
}

export interface State {
  user: UserState;
}
