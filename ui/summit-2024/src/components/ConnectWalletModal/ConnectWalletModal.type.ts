interface ConnectWalletContextType {
  isMobile: boolean;
  meerkatAddress: string | undefined;
}

type ConnectWalletProps = {
  showPeerConnect: boolean;
  handleCloseConnectWalletModal: (open?: boolean) => void;
};

export { ConnectWalletContextType, ConnectWalletProps };
