import React from 'react';
import moment from 'moment';
import toast from 'react-hot-toast';
import { useDispatch, useSelector } from 'react-redux';
import { Box } from '@mui/material';
import CssBaseline from '@mui/material/CssBaseline';
import { ConnectWalletModal } from 'components/ConnectWalletModal/ConnectWalletModal';
import { VoteSubmittedModal } from 'components/VoteSubmittedModal/VoteSubmittedModal';
import { PageRouter } from 'common/routes';
import { RootState } from 'common/store';
import { setIsConnectWalletModalVisible, setIsVoteSubmittedModalVisible } from 'common/store/userSlice';
import { EVENT_END_TIME, EVENT_END_TIME_FORMAT } from 'common/constants/appConstants';
import styles from './Content.module.scss';

export default function Content() {
  const date = EVENT_END_TIME;
  const endTime = moment(date, EVENT_END_TIME_FORMAT).format('MMMM Do');
  const isConnectWalletModalVisible = useSelector((state: RootState) => state.user.isConnectWalletModalVisible);
  const isVoteSubmittedModalVisible = useSelector((state: RootState) => state.user.isVoteSubmittedModalVisible);
  const dispatch = useDispatch();

  const onConnectWallet = () => {
    dispatch(setIsConnectWalletModalVisible({ isVisible: false }));
    toast('Wallet Connected!');
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
      <VoteSubmittedModal
        openStatus={isVoteSubmittedModalVisible}
        onCloseFn={() => {
          dispatch(setIsVoteSubmittedModalVisible({ isVisible: false }));
        }}
        name="vote-submitted-modal"
        id="vote-submitted-modal"
        title="Vote submitted"
        description={
          <>
            <div style={{ marginBottom: '10px' }}>Thank you, your vote has been submitted.</div>
            Make sure to check back on <b>{endTime}</b> to see the results!
          </>
        }
      />
    </Box>
  );
}
