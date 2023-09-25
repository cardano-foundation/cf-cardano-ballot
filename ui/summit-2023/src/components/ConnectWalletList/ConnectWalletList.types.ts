interface IWalletInfo {
  address?: string;
  name: string;
  version: string;
  icon: string;
  requestAutoconnect?: boolean;
}

export { IWalletInfo };
