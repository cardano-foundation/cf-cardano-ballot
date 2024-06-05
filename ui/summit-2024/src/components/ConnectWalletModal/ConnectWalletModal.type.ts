interface ConnectWalletContextType {
  isMobile: boolean;
  meerkatAddress: string | undefined;
  peerConnectWalletInfo: string | undefined;
}

type ConnectWalletProps = {
  showPeerConnect: boolean;
  handleCloseConnectWalletModal: (open?: boolean) => void;
};

export type { ConnectWalletContextType, ConnectWalletProps };
