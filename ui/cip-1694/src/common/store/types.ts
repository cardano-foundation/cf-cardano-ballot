import { ChainTip, EventPresentation } from 'types/voting-ledger-follower-types';

export interface UserState {
  isConnectWalletModalVisible: boolean;
  isVoteSubmittedModalVisible: boolean;
  isCommingSoonModalVisible: boolean;
  isMobileMenuVisible: boolean;
  connectedWallet: string;
  event: EventPresentation;
  tip: ChainTip;
}

export interface State {
  user: UserState;
}
