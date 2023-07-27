/* eslint-disable indent */
import React, { useEffect } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import toast from 'react-hot-toast';
import { Button } from '@mui/material';
import AccountBalanceWalletIcon from '@mui/icons-material/AccountBalanceWallet';
import KeyboardArrowDownIcon from '@mui/icons-material/KeyboardArrowDown';
import {
  useCardano,
  ConnectWalletButton as CFConnectWalletButton,
  getWalletIcon,
} from '@cardano-foundation/cardano-connect-with-wallet';
import {
  setConnectedWallet,
  setIsConnectWalletModalVisible,
  setIsReceiptFetched,
  setVoteReceipt,
} from 'common/store/userSlice';
import { ALWAYS_VISIBLE_WALLETS, SUPPORTED_WALLETS } from 'common/constants/appConstants';
import { RootState } from 'common/store';
import styles from './Header.module.scss';

export const ConnectWalletButton = () => {
  const { disconnect, stakeAddress, enabledWallet } = useCardano();
  const dispatch = useDispatch();
  const connectedWallet = useSelector((state: RootState) => state.user.connectedWallet);

  const supportedWallets = SUPPORTED_WALLETS;
  const alwaysVisibleWallets = ALWAYS_VISIBLE_WALLETS;

  // TODO: move to providers level and throw?
  useEffect(() => {
    if (supportedWallets.length === 0) {
      toast('No supported wallets specified!');
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  useEffect(() => {
    const init = async () => {
      if (enabledWallet) {
        dispatch(setConnectedWallet({ wallet: enabledWallet }));
      }
    };
    init();
  }, [dispatch, enabledWallet, stakeAddress]);

  const onConnectWallet = (walletName: string) => {
    console.log(walletName);
    dispatch(setIsConnectWalletModalVisible({ isVisible: false }));
  };

  const onDisconnectWallet = () => {
    disconnect();
    dispatch(setConnectedWallet({ wallet: '' }));
    dispatch(setVoteReceipt({ receipt: null }));
    dispatch(setIsReceiptFetched({ isFetched: false }));
  };

  return !connectedWallet ? (
    <Button
      size="large"
      variant="contained"
      className={styles.connectButton}
      onClick={() => dispatch(setIsConnectWalletModalVisible({ isVisible: true }))}
    >
      <AccountBalanceWalletIcon className={styles.walletIcon} />
      <span>Connect wallet</span>
    </Button>
  ) : (
    <CFConnectWalletButton
      label="Connect wallet"
      borderRadius={8}
      onConnect={onConnectWallet}
      onDisconnect={onDisconnectWallet}
      alwaysVisibleWallets={alwaysVisibleWallets}
      supportedWallets={supportedWallets}
      beforeComponent={
        <img
          className={styles.walletIcon}
          src={getWalletIcon(connectedWallet)}
          alt="wallet-icon"
        />
      }
      afterComponent={<KeyboardArrowDownIcon className={styles.arrowIcon} />}
      customCSS={`
        width: 170px;
        margin-left: 10px;
        button {
          background: transparent !important;
          color: #061D3C !important;
          border: 1px solid #BBB !important;
          padding-left: 15px;
        }
        span {
          color: #F5F9FF;
          background: #061D3C;
          border-radius: 8px;
          padding: 0 25px;
          : hover {
            color: #F5F9FF;
            background: #061D3C;
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
