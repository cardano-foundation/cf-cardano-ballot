import { IWalletInfo } from "../ConnectWalletList/ConnectWalletList.types";

interface ConnectWalletContextType {
  peerConnectWalletInfo: IWalletInfo | undefined;
}

type ConnectWalletProps = {
  showPeerConnect: boolean;
  handleCloseConnectWalletModal: (open?: boolean) => void;
};

export { ConnectWalletContextType, ConnectWalletProps };
