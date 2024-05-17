import React from 'react';
import { Avatar, List, ListItem, ListItemAvatar, Typography } from '@mui/material';
import PhonelinkRingIcon from '@mui/icons-material/PhonelinkRing';
import { useCardano } from '@cardano-foundation/cardano-connect-with-wallet';
import './ConnectWalletList.scss';
import { resolveCardanoNetwork, walletIcon } from '../../utils/utils';
import { env } from 'common/constants/env';

type ConnectWalletModalProps = {
  description: string;
  onConnectWallet: () => void;
  onConnectError: (code: Error) => void;
  onOpenPeerConnect: () => void;
};

const SUPPORTED_WALLETS = env.SUPPORTED_WALLETS;

const ConnectWalletList = (props: ConnectWalletModalProps) => {
  const { description, onConnectWallet, onConnectError, onOpenPeerConnect } = props;
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
        {availableWallets.length ? (
          availableWallets.map((walletName, index) => (
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
                Connect <span className="walletName">{walletName === 'typhoncip30' ? 'typhon' : walletName}</span>{' '}
                Wallet
              </Typography>
            </ListItem>
          ))
        ) : (
          <>
            <Typography className="walletLabel">No extension wallets installed</Typography>
          </>
        )}
        <ListItem
          className="walletItem"
          onClick={() => onOpenPeerConnect()}
        >
          <ListItemAvatar>
            <PhonelinkRingIcon style={{ width: '24px', height: '24px' }} />
          </ListItemAvatar>
          <Typography className="walletLabel">
            Connect <span className="walletName">P2P</span> wallet (Beta)
          </Typography>
        </ListItem>
      </List>
    </>
  );
};

export default ConnectWalletList;
