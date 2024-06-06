interface IWalletInfo {
  address?: string;
  name: string;
  version: string;
  icon: string;
  requestAutoconnect?: boolean;
}

enum ConnectWalletFlow {
  SELECT_WALLET = "SELECT_WALLET",
  CONNECT_IDENTITY_WALLET = "CONNECT_IDENTITY_WALLET",
  ACCEPT_CONNECTION = "ACCEPT_CONNECTION",
  CONNECT_CIP45_WALLET = "CONNECT_CIP45_WALLET",
}

enum NetworkType {
  MAINNET = "mainnet",
  TESTNET = "testnet",
}

export { ConnectWalletFlow, NetworkType };
export type { IWalletInfo };
