import React, { useEffect, useState } from 'react';
import AccountBalanceWalletIcon from '@mui/icons-material/AccountBalanceWallet';
import { useCardano, ConnectWalletButton, getWalletIcon } from '@cardano-foundation/cardano-connect-with-wallet';

export const ConnectWallet = () => {
  const { disconnect, stakeAddress, enabledWallet } = useCardano();
  const [walletIcon, setWalletIcon] = useState('');

  useEffect(() => {
    const init = async () => {
      if (enabledWallet && enabledWallet.length) {
        setWalletIcon(getWalletIcon(enabledWallet));
      }
    };
    init();
  }, [enabledWallet, stakeAddress]);

  const onConnectWallet = (walletName: string) => {
    console.log(walletName);
  };

  return (
    <ConnectWalletButton
      label="Connect wallet"
      borderRadius={8}
      onConnect={onConnectWallet}
      onDisconnect={() => {
        disconnect();
        setWalletIcon('');
      }}
      alwaysVisibleWallets={['lace']}
      supportedWallets={['flint', 'eternl', 'nami', 'typhon', 'yoroi', 'nufi', 'gerowallet', 'lace']}
      beforeComponent={
        walletIcon.length ? (
          <img
            height={22}
            width={22}
            style={{ marginRight: '10px' }}
            src={walletIcon}
            alt=""
          />
        ) : (
          <AccountBalanceWalletIcon
            style={{ marginRight: '10px' }}
            height={22}
            width={22}
          />
        )
      }
      customCSS={`
        width: 170px;
        margin-left: 10px;
        button {
          background: #061D3C;
          "Roboto","Helvetica","Arial",sans-serif;
          font-size: 16px;
          font-style: normal;
          font-weight: 600;
          line-height: normal;
          height: 49px;
          padding-left: 15px;
          color: #F5F9FF;
        }
        span {
          padding: 16px;
          font-family: Helvetica Light,sans-serif;
          font-size: 0.875rem;
        }
      `}
    />
  );
};
