import React, { useCallback, useEffect, useState } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { v4 as uuidv4 } from 'uuid';
import toast from 'react-hot-toast';
import cn from 'classnames';
import { Grid, Typography, Button } from '@mui/material';
import DoneIcon from '@mui/icons-material/Done';
import CloseIcon from '@mui/icons-material/Close';
import DoDisturbIcon from '@mui/icons-material/DoDisturb';
import ReceiptIcon from '@mui/icons-material/ReceiptLongOutlined';
import { useCardano } from '@cardano-foundation/cardano-connect-with-wallet';
import CountDownTimer from 'components/CountDownTimer/CountDownTimer';
import {
  setIsConnectWalletModalVisible,
  setIsReceiptFetched,
  setIsVoteSubmittedModalVisible,
  setVoteReceipt,
} from 'common/store/userSlice';
import { Account, SignedWeb3Request } from 'types/backend-services-types';
import { RootState } from 'common/store';
import { OptionCard } from '../../components/OptionCard/OptionCard';
import { OptionItem } from '../../components/OptionCard/OptionCard.types';
import SidePage from '../../components/common/SidePage/SidePage';
import { buildCanonicalVoteInputJson, buildCanonicalVoteReceiptInputJson } from '../../common/utils/voteUtils';
import * as voteService from '../../common/api/voteService';
import { EVENT_ID } from '../../common/constants/appConstants';
import { useToggle } from '../../common/hooks/useToggle';
import { HttpError } from '../../common/handlers/httpHandler';
import VoteReceipt from './VoteReceipt';
import styles from './Vote.module.scss';

const errorsMap = {
  INVALID_VOTING_POWER: 'To cast a vote, Voting Power should be more than 0',
  EXPIRED_SLOT: "CIP-93's envelope slot is expired!",
  VOTE_CANNOT_BE_CHANGED: 'You have already voted! Vote cannot be changed for this stake address',
};

const items: OptionItem[] = [
  {
    label: 'yes',
    icon: <DoneIcon sx={{ fontSize: 52, color: '#39486C' }} />,
  },
  {
    label: 'no',
    icon: <CloseIcon sx={{ fontSize: 52, color: '#39486C' }} />,
  },
  {
    label: 'abstain',
    icon: <DoDisturbIcon sx={{ fontSize: 52, color: '#39486C' }} />,
  },
];

const Vote = () => {
  const { stakeAddress, isConnected, signMessage } = useCardano();
  const receipt = useSelector((state: RootState) => state.user.receipt);
  const isReceiptFetched = useSelector((state: RootState) => state.user.isReceiptFetched);
  const [absoluteSlot, setAbsoluteSlot] = useState<number>();
  const [optionId, setOptionId] = useState('');
  const [isToggledReceipt, toggleReceipt] = useToggle(false);
  const dispatch = useDispatch();

  const getSignedMessagePromise = useCallback(
    async (message: string): Promise<SignedWeb3Request> =>
      new Promise((resolve, reject) => {
        signMessage(
          message,
          (signature, key) => resolve({ coseSignature: signature, cosePublicKey: key || '' }),
          (error: Error) => reject(error)
        );
      }),
    [signMessage]
  );

  const fetchReceipt = useCallback(async () => {
    try {
      const requestVoteObjectPayload = await getSignedMessagePromise(
        buildCanonicalVoteReceiptInputJson({
          voter: stakeAddress,
          slotNumber: absoluteSlot.toString(),
        })
      );
      const receiptResponse = await voteService.getVoteReceipt(requestVoteObjectPayload);
      if ('id' in receiptResponse) {
        dispatch(setVoteReceipt({ receipt: receiptResponse }));
      } else {
        const message = `Failed to fetch receipt', ${receiptResponse?.title}, ${receiptResponse?.detail}`;
        console.log(message);
      }
      dispatch(setIsReceiptFetched({ isFetched: true }));
    } catch (error) {
      if (error?.message === 'VOTE_NOT_FOUND') {
        dispatch(setVoteReceipt({ receipt: null }));
        dispatch(setIsReceiptFetched({ isFetched: true }));
        return;
      }
      const message = `Failed to fetch receipt, ${error?.info || error?.message || error?.toString()}`;
      toast.error(message);
      console.log(message);
    }
  }, [absoluteSlot, dispatch, getSignedMessagePromise, stakeAddress]);

  const init = useCallback(async () => {
    try {
      setAbsoluteSlot((await voteService.getSlotNumber())?.absoluteSlot);
    } catch (error) {
      console.log('Failed to fecth slot number', error?.message);
    }
  }, []);

  useEffect(() => {
    if (isConnected) {
      init();
    }
  }, [init, isConnected]);

  useEffect(() => {
    if (absoluteSlot) {
      fetchReceipt();
    }
  }, [absoluteSlot, fetchReceipt]);

  const onChangeOption = (option: string | null) => {
    setOptionId(option);
  };

  const handleSubmit = async () => {
    if (!EVENT_ID) {
      console.log('EVENT_ID is not provided');
      return;
    }

    if (!isConnected) {
      dispatch(setIsConnectWalletModalVisible({ isVisible: true }));
      return;
    }

    let votingPower: Account['votingPower'];
    try {
      ({ votingPower } = await voteService.getVotingPower(EVENT_ID, stakeAddress));
    } catch (error) {
      const message = `Failed to fetch votingPower ${
        error instanceof Error || error instanceof HttpError ? error?.message : error
      }`;

      console.log(message);
      toast.error(message);
      return;
    }

    const canonicalVoteInput = buildCanonicalVoteInputJson({
      option: optionId?.toUpperCase(),
      voter: stakeAddress,
      voteId: uuidv4(),
      slotNumber: absoluteSlot.toString(),
      votePower: votingPower,
    });

    try {
      const requestVoteObject = await getSignedMessagePromise(canonicalVoteInput);
      await voteService.castAVoteWithDigitalSignature(requestVoteObject);
      dispatch(setIsVoteSubmittedModalVisible({ isVisible: true }));
      await fetchReceipt();
    } catch (error) {
      if (error instanceof HttpError && error.code === 400) {
        toast.error(errorsMap[error?.message as keyof typeof errorsMap] || error?.message);
        setOptionId('');
      } else if (error instanceof Error) {
        toast.error(error?.message || error.toString());
        console.log('Failed to cast e vote', error);
      }
    }
  };

  return (
    <div className={styles.vote}>
      <Grid
        container
        direction="column"
        justifyContent="left"
        alignItems="left"
        spacing={0}
      >
        <Grid item>
          <Typography
            variant="h5"
            className={styles.title}
          >
            CIP-1694 vote
          </Typography>
        </Grid>
        <Grid item>
          <Typography
            sx={{
              mb: '24px',
            }}
          >
            <CountDownTimer />
          </Typography>
        </Grid>
        <Grid item>
          <Typography
            variant="h5"
            className={styles.description}
          >
            Do you want CIP-1694 that will allow On-Chain Governance, implemented on the Cardano Blockchain?
          </Typography>
        </Grid>
        <Grid item>
          <OptionCard
            selectedOption={receipt?.proposal?.toLowerCase()}
            disabled={!!receipt}
            items={items}
            onChangeOption={onChangeOption}
          />
        </Grid>
        <Grid item>
          <Grid
            container
            direction="row"
            justifyContent={'center'}
          >
            <Grid item>
              {!receipt?.id ? (
                <Button
                  className={cn(styles.button, { [styles.disabled]: (!optionId && isConnected) || !isReceiptFetched })}
                  size="large"
                  variant="contained"
                  disabled={!optionId && isConnected}
                  onClick={() => handleSubmit()}
                  sx={{}}
                >
                  {!isConnected ? 'Connect wallet to vote' : 'Submit Your Vote'}
                </Button>
              ) : (
                <Button
                  className={cn(styles.button, styles.secondary)}
                  variant="contained"
                  onClick={() => toggleReceipt()}
                  aria-label="Receipt"
                  startIcon={<ReceiptIcon />}
                >
                  Vote receipt
                </Button>
              )}
            </Grid>
          </Grid>
        </Grid>
      </Grid>
      <SidePage
        anchor="right"
        open={isToggledReceipt}
        setOpen={toggleReceipt}
      >
        <VoteReceipt />
      </SidePage>
    </div>
  );
};

export default Vote;
