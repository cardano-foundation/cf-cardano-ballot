import React from 'react';
import {Avatar, List, ListItem, ListItemAvatar, Typography} from '@mui/material';
import { useCardano } from '@cardano-foundation/cardano-connect-with-wallet';
import './ConnectWalletList.scss';
import { walletIcon } from '../../utils/utils';
import { NetworkType } from '@cardano-foundation/cardano-connect-with-wallet-core';

type ConnectWalletModalProps = {
  description: string;
  onConnectWallet: () => void;
  onConnectError: () => void;
};

// TODO: move to .env file
// TODO: handle mobile support. Flint
const SUPPORTED_WALLETS = ['flint', 'nami', 'eternl', 'typhon', 'yoroi', 'nufi'];

const ConnectWalletList = (props: ConnectWalletModalProps) => {
  const { description, onConnectWallet, onConnectError } = props;
  const { installedExtensions, connect } = useCardano({ limitNetwork: 'testnet' as NetworkType });

  const availableWallets = installedExtensions.filter((installedWallet) => SUPPORTED_WALLETS.includes(installedWallet));

  return (
    <>
      <Typography
        className="connect-wallet-modal-description"
        gutterBottom
        style={{ wordWrap: 'break-word' }}
      >
        {description}
      </Typography>
      <List>
        {availableWallets.map((walletName, index) => (
          <ListItem
            key={index}
            className="walletItem"
            onClick={() => connect(walletName, onConnectWallet, onConnectError)}
          >
            <ListItemAvatar>
              <Avatar
                src={walletIcon(walletName)}
                style={{ width: '24px', height: '24px' }}
              />
            </ListItemAvatar>
            <Typography className="walletLabel">
              Connect <span className="walletName">{walletName}</span> wallet
            </Typography>
          </ListItem>
        ))}
      </List>
    </>
  );
};

export default ConnectWalletList;
