export interface UserState {
  isConnectWalletModalVisible: boolean;
  isVoteSubmittedModalVisible: boolean;
  connectedWallet: string;
}

export interface State {
  user: UserState;
}
