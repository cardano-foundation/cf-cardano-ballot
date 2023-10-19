/* eslint-disable indent */
import React, { useEffect } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import cn from 'classnames';
import { Button } from '@mui/material';
import AccountBalanceWalletIcon from '@mui/icons-material/AccountBalanceWallet';
import KeyboardArrowDownIcon from '@mui/icons-material/KeyboardArrowDown';
import {
  useCardano,
  ConnectWalletButton as CFConnectWalletButton,
  getWalletIcon,
} from '@cardano-foundation/cardano-connect-with-wallet';
import { setConnectedWallet, setIsConnectWalletModalVisible } from 'common/store/userSlice';
import { RootState } from 'common/store';
import { clearUserInSessionStorage } from 'common/utils/session';
import { resolveCardanoNetwork } from 'common/utils/common';
import styles from './ConnectWalletButton.module.scss';
import { env } from '../../../../env';

export const ConnectWalletButton = ({ isMobileMenu = false }) => {
  const { disconnect, stakeAddress, enabledWallet, installedExtensions } = useCardano({
    limitNetwork: resolveCardanoNetwork(env.TARGET_NETWORK),
  });
  const dispatch = useDispatch();
  const connectedWallet = useSelector((state: RootState) => state.user.connectedWallet);

  const supportedWallets = installedExtensions.filter((installedWallet) =>
    env.SUPPORTED_WALLETS.includes(installedWallet)
  );

  useEffect(() => {
    const init = async () => {
      if (enabledWallet) {
        dispatch(setConnectedWallet({ wallet: enabledWallet }));
      }
    };
    init();
  }, [dispatch, enabledWallet, stakeAddress]);

  const onConnectWallet = () => {
    dispatch(setIsConnectWalletModalVisible({ isVisible: false }));
  };

  const onDisconnectWallet = () => {
    disconnect();
    dispatch(setConnectedWallet({ wallet: '' }));
    clearUserInSessionStorage();
  };

  return !connectedWallet ? (
    <Button
      size="large"
      variant="contained"
      className={cn(styles.connectButton, { [styles.isMobileMenu]: isMobileMenu })}
      onClick={() => dispatch(setIsConnectWalletModalVisible({ isVisible: true }))}
      data-testid="connect-wallet-button"
    >
      <AccountBalanceWalletIcon className={styles.walletIcon} />
      <span>Connect wallet</span>
    </Button>
  ) : (
    <CFConnectWalletButton
      showUnavailableWallets={0}
      data-testid="connected-wallet-button"
      label="Connect wallet"
      borderRadius={8}
      onConnect={onConnectWallet}
      onDisconnect={onDisconnectWallet}
      supportedWallets={supportedWallets}
      beforeComponent={
        <img
          className={styles.walletIcon}
          src={getWalletIcon(connectedWallet)}
          alt="wallet-icon"
        />
      }
      afterComponent={
        <KeyboardArrowDownIcon className={cn(styles.arrowIcon, { [styles.isMobileMenu]: isMobileMenu })} />
      }
      customCSS={`
        width: ${isMobileMenu ? '170px' : '100%'};
        padding-top: 0;
        button {
          background: transparent !important;
          color: #061D3C !important;
          border: 1px solid #BBB !important;
          padding-left: 15px;
        }
        span {
          color: #F5F9FF;
          background: #1D439B;
          border-radius: 8px;
          padding: 0 25px;
          : hover {
            color: #F5F9FF;
            background: #1D439B;
          }
        }
        span, button {
          height: 49px;
          max-height: 49px;
          font-size: 16px;
          font-style: normal;
          font-weight: 600;
          line-height: normal;
        }
        :hover button {
          :hover {
            border: 1px solid #1D439B !important;
            background: rgba(29, 67, 155, 0.10) !important;
          }
        }
      `}
    />
  );
};
