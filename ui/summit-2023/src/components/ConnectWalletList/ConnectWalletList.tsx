import React from 'react';
import { Avatar, List, ListItem, ListItemAvatar, Typography } from '@mui/material';
import { useCardano } from '@cardano-foundation/cardano-connect-with-wallet';
import './ConnectWalletList.scss';
import { resolveCardanoNetwork, walletIcon } from '../../utils/utils';
import { env } from 'common/constants/env';

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
  const { installedExtensions, connect } = useCardano({ limitNetwork: resolveCardanoNetwork(env.TARGET_NETWORK) });

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
              Connect <span className="walletName">{walletName}</span> Wallet
            </Typography>
          </ListItem>
        ))}
      </List>
    </>
  );
};

export default ConnectWalletList;
