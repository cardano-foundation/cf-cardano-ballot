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

type ConnectWalletButtonProps = {
  disableBackdropClick?: boolean;
  onOpenConnectWalletModal: () => void;
  onOpenVerifyWalletModal: () => void;
};

const ConnectWalletButton = (props: ConnectWalletButtonProps) => {
  const { onOpenConnectWalletModal, onOpenVerifyWalletModal } = props;
  const eventCache = useSelector((state: RootState) => state.user.event);
  const walletIsVerified = useSelector((state: RootState) => state.user.walletIsVerified);

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
          <Button
            sx={{ zIndex: '99', cursor: walletIsVerified ? 'default' : 'pointer' }}
            className="connect-button verify-button"
            color="inherit"
            onClick={() => onOpenVerifyWalletModal()}
            disabled={eventCache?.finished}
          >
            {walletIsVerified ? (
              <>
                <span style={{ paddingTop: '3px' }}>Verified</span>{' '}
                <VerifiedIcon style={{ width: '20px', paddingBottom: '0px', color: '#1C9BEF' }} />{' '}
              </>
            ) : (
              'Verify'
            )}
          </Button>
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
