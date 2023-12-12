import React, { useCallback } from 'react';
import toast from 'react-hot-toast';
import { useDispatch, useSelector } from 'react-redux';
import { Box, debounce } from '@mui/material';
import CssBaseline from '@mui/material/CssBaseline';
import BlockIcon from '@mui/icons-material/Block';
import { useCardano } from '@cardano-foundation/cardano-connect-with-wallet';
import { PageRoutes } from 'common/routes';
import { RootState } from 'common/store';
import {
  setIsConnectWalletModalVisible,
  setIsCommingSoonModalVisible,
  setIsMobileMenuVisible as setIsMobileMenuVisibleAction,
} from 'common/store/userSlice';
import { clearUserInSessionStorage } from 'common/utils/session';
import { getDateAndMonth } from 'common/utils/dateUtils';
import { ConnectWalletModal } from 'components/ConnectWalletModal/ConnectWalletModal';
import { formatUTCDate } from 'pages/Leaderboard/utils';
import { ResultsCommingSoonModal } from 'pages/Leaderboard/components/ResultsCommingSoonModal/ResultsCommingSoonModal';
import { Toast } from 'components/Toast/Toast';
import { env } from '../../env';
import styles from './Content.module.scss';

export const Content = () => {
  const { installedExtensions } = useCardano();
  const event = useSelector((state: RootState) => state.user.event);
  const isConnectWalletModalVisible = useSelector((state: RootState) => state.user.isConnectWalletModalVisible);
  const isCommingSoonModalVisible = useSelector((state: RootState) => state.user.isCommingSoonModalVisible);
  const dispatch = useDispatch();
  const setIsMobileMenuVisible = useCallback(
    (isVisible: boolean) => {
      dispatch(setIsMobileMenuVisibleAction({ isVisible }));
    },
    [dispatch]
  );

  // eslint-disable-next-line react-hooks/exhaustive-deps
  const debouncedToast = useCallback(debounce(toast), []);

  // FIXME: triggered multiple times on connect for Flint wallet by @cardano-foundation/cardano-connect-with-wallet
  const onConnectWallet = useCallback(() => {
    clearUserInSessionStorage();
    dispatch(setIsConnectWalletModalVisible({ isVisible: false }));
    debouncedToast(<Toast message="Wallet Connected!" />);
  }, [dispatch, debouncedToast]);

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
      {isConnectWalletModalVisible && (
        <ConnectWalletModal
          installedExtensions={installedExtensions}
          openStatus={isConnectWalletModalVisible}
          onCloseFn={onCloseFn}
          name="connect-wallet-list"
          id="connect-wallet-list"
          title="Connect wallet"
          description={
            <>
              In order to participate, first you will need to connect your wallet. Following wallets are accepted:{' '}
              <span style={{ fontWeight: '500' }}>
                {env.SUPPORTED_WALLETS?.map((w) => {
                  const walletName = w.replace('typhoncip30', 'Typhon');
                  return `${walletName[0].toUpperCase()}${walletName.slice(1)}`;
                })?.join(', ')}
              </span>
              .
            </>
          }
          onConnectWallet={onConnectWallet}
          onConnectWalletError={onConnectWalletError}
        />
      )}
      {isCommingSoonModalVisible && (
        <ResultsCommingSoonModal
          openStatus={isCommingSoonModalVisible}
          onCloseFn={() => {
            dispatch(setIsCommingSoonModalVisible({ isVisible: false }));
            setIsMobileMenuVisible(false);
          }}
          onGoBackFn={() => {
            dispatch(setIsCommingSoonModalVisible({ isVisible: false }));
            setIsMobileMenuVisible(false);
          }}
          name="vote-submitted-modal"
          id="vote-submitted-modal"
          title="Coming soon"
          description={
            <>
              The results will be available from{' '}
              <b>
                {event?.proposalsRevealDate && getDateAndMonth(event?.proposalsRevealDate?.toString())}{' '}
                {formatUTCDate(event?.proposalsRevealDate?.toString())}
              </b>
            </>
          }
        />
      )}
    </Box>
  );
};
