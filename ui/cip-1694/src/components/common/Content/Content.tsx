import React, { useCallback } from 'react';
import toast from 'react-hot-toast';
import { useDispatch, useSelector } from 'react-redux';
import { Box, debounce } from '@mui/material';
import CssBaseline from '@mui/material/CssBaseline';
import { ConnectWalletModal } from 'components/ConnectWalletModal/ConnectWalletModal';
import { PageRouter } from 'common/routes';
import { RootState } from 'common/store';
import { setIsConnectWalletModalVisible } from 'common/store/userSlice';
import styles from './Content.module.scss';

export default function Content() {
  const isConnectWalletModalVisible = useSelector((state: RootState) => state.user.isConnectWalletModalVisible);
  const dispatch = useDispatch();

  // eslint-disable-next-line react-hooks/exhaustive-deps
  const debouncedToast = useCallback(debounce(toast), []);

  // FIXME: triggered multiple times on connect
  const onConnectWallet = () => {
    dispatch(setIsConnectWalletModalVisible({ isVisible: false }));
    debouncedToast('Wallet Connected!');
  };

  return (
    <Box className={styles.content}>
      <CssBaseline />
      <PageRouter />
      <ConnectWalletModal
        openStatus={isConnectWalletModalVisible}
        onCloseFn={() => {
          dispatch(setIsConnectWalletModalVisible({ isVisible: false }));
        }}
        name="connect-wallet-list"
        id="connect-wallet-list"
        title="Connect wallet"
        description="In order to vote, first you will need to connect your wallet."
        onConnectWallet={onConnectWallet}
      />
    </Box>
  );
}
