import { useSelector } from 'react-redux';
import { RootState } from '../../../store';
import { useCardano } from '@cardano-foundation/cardano-connect-with-wallet';
import { eventBus } from '../../../utils/EventBus';
import { Avatar, Button } from '@mui/material';
import { addressSlice, resolveCardanoNetwork, walletIcon } from '../../../utils/utils';
import AccountBalanceWalletIcon from '@mui/icons-material/AccountBalanceWallet';
import KeyboardArrowDownIcon from '@mui/icons-material/KeyboardArrowDown';
import { i18n } from '../../../i18n';
import VerifiedIcon from '@mui/icons-material/Verified';
import React from 'react';
import './ConnectWalletButton.scss';
import { env } from 'common/constants/env';
import {clearUserInSessionStorage, getUserInSession, tokenIsExpired} from '../../../utils/session';

type ConnectWalletButtonProps = {
  disableBackdropClick?: boolean;
  onOpenConnectWalletModal: () => void;
  onOpenVerifyWalletModal: () => void;
  onLogin: () => void;
};

const ConnectWalletButton = (props: ConnectWalletButtonProps) => {
  const { onOpenConnectWalletModal, onOpenVerifyWalletModal, onLogin } = props;
  const eventCache = useSelector((state: RootState) => state.user.event);
  const walletIsVerified = useSelector((state: RootState) => state.user.walletIsVerified);
  const session = getUserInSession();
  const isExpired = tokenIsExpired(session?.expiresAt);

  const { stakeAddress, isConnected, disconnect, enabledWallet } = useCardano({
    limitNetwork: resolveCardanoNetwork(env.TARGET_NETWORK),
  });

  const handleConnectWallet = () => {
    if (!isConnected) {
      onOpenConnectWalletModal();
    }
  };

  const onDisconnectWallet = () => {
    disconnect();
    clearUserInSessionStorage();
    eventBus.publish('showToast', 'Wallet disconnected successfully');
  };

  return (
    <div className="button-container">
      <Button
        sx={{ zIndex: '99' }}
        className={isConnected ? 'connected-button' : 'connect-button'}
        color="inherit"
        onClick={() => handleConnectWallet()}
      >
        {isConnected && enabledWallet ? (
          <Avatar
            src={walletIcon(enabledWallet)}
            style={{ width: '24px', height: '24px' }}
          />
        ) : (
          <AccountBalanceWalletIcon />
        )}
        {isConnected ? (
          <>
            {stakeAddress ? addressSlice(stakeAddress, 5) : null}
            {walletIsVerified ? (
              <VerifiedIcon style={{ width: '20px', paddingBottom: '0px', color: '#1C9BEF' }} />
            ) : null}
            <div className="arrow-icon">
              <KeyboardArrowDownIcon />
            </div>
          </>
        ) : (
          <>
            <span> {i18n.t('header.connectWalletButton')}</span>
          </>
        )}
      </Button>
      {isConnected && (
        <div className="disconnect-wrapper">
          {!walletIsVerified ? (
            <Button
              sx={{ zIndex: '99', cursor: walletIsVerified ? 'default' : 'pointer' }}
              className="connect-button verify-button"
              color="inherit"
              onClick={() => onOpenVerifyWalletModal()}
              disabled={eventCache?.finished}
            >
              Verify
            </Button>
          ) : null}
          {walletIsVerified || (!walletIsVerified && eventCache.finished) ? (
            <Button
              sx={{ zIndex: '99', cursor: 'pointer' }}
              className="connect-button verify-button"
              color="inherit"
              onClick={() => onLogin()}
              disabled={session && !isExpired}
            >
              Login
            </Button>
          ) : null}
          <Button
            sx={{ zIndex: '99' }}
            className="connect-button disconnect-button"
            color="inherit"
            onClick={onDisconnectWallet}
          >
            Disconnect Wallet
          </Button>
        </div>
      )}
    </div>
  );
};

export { ConnectWalletButton };
