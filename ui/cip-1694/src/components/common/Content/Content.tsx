import React, { useCallback } from 'react';
import toast from 'react-hot-toast';
import { useDispatch, useSelector } from 'react-redux';
import { Box } from '@mui/material';
import CssBaseline from '@mui/material/CssBaseline';
import BlockIcon from '@mui/icons-material/Block';
import { ConnectWalletModal } from 'components/ConnectWalletModal/ConnectWalletModal';
import { PageRoutes } from 'common/routes';
import { RootState } from 'common/store';
import { setIsConnectWalletModalVisible } from 'common/store/userSlice';
import styles from './Content.module.scss';
import { Toast } from '../Toast/Toast';

export const Content = () => {
  const isConnectWalletModalVisible = useSelector((state: RootState) => state.user.isConnectWalletModalVisible);
  const dispatch = useDispatch();

  // FIXME: triggered multiple times on connect
  const onConnectWallet = useCallback(() => {
    dispatch(setIsConnectWalletModalVisible({ isVisible: false }));
    toast(<Toast message="Wallet Connected!" />);
  }, [dispatch]);

  const onConnectWalletError = useCallback(() => {
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
