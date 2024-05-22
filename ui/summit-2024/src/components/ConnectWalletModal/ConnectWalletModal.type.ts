
interface ConnectWalletContextType {
  isMobile: boolean
}

type ConnectWalletProps = {
  showPeerConnect: boolean;
  handleCloseConnectWalletModal: (open?: boolean) => void;
};

export { ConnectWalletContextType, ConnectWalletProps };
