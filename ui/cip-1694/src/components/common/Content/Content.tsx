import React, { useCallback } from 'react';
import toast from 'react-hot-toast';
import { useDispatch, useSelector } from 'react-redux';
import { Box, debounce } from '@mui/material';
import CssBaseline from '@mui/material/CssBaseline';
import BlockIcon from '@mui/icons-material/Block';
import { ConnectWalletModal } from 'components/ConnectWalletModal/ConnectWalletModal';
import { PageRoutes } from 'common/routes';
import { RootState } from 'common/store';
import { setIsConnectWalletModalVisible } from 'common/store/userSlice';
import { clearUserInSessionStorage } from 'common/utils/session';
import styles from './Content.module.scss';
import { Toast } from '../Toast/Toast';

export const Content = () => {
  const isConnectWalletModalVisible = useSelector((state: RootState) => state.user.isConnectWalletModalVisible);
  const dispatch = useDispatch();

  // eslint-disable-next-line react-hooks/exhaustive-deps
  const debouncedToast = useCallback(debounce(toast), []);

  // FIXME: triggered multiple times on connect for Flint wallet by @cardano-foundation/cardano-connect-with-wallet
  const onConnectWallet = useCallback(() => {
    clearUserInSessionStorage();
    dispatch(setIsConnectWalletModalVisible({ isVisible: false }));
    debouncedToast(<Toast message="Wallet Connected!" />);
  }, [dispatch, debouncedToast]);

  const onConnectWalletError = useCallback((walletName: string, error: Error) => {
    console.log(walletName, error);
    toast(
      <Toast
        error
        message={'Unable to connect your wallet. Please try again'}
        icon={<BlockIcon style={{ fontSize: '19px', color: '#F5F9FF' }} />}
      />
    );
  }, []);

  const onCloseFn = useCallback(() => dispatch(setIsConnectWalletModalVisible({ isVisible: false })), [dispatch]);

  return (
    <Box className={styles.content}>
      <CssBaseline />
      <PageRoutes />
      <ConnectWalletModal
        openStatus={isConnectWalletModalVisible}
        onCloseFn={onCloseFn}
        name="connect-wallet-list"
        id="connect-wallet-list"
        title="Connect wallet"
        description="In order to vote, first you will need to connect your wallet."
        onConnectWallet={onConnectWallet}
        onConnectWalletError={onConnectWalletError}
      />
    </Box>
  );
};
