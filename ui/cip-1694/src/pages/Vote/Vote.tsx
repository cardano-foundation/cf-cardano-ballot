import React, { useCallback, useEffect, useState } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { Link } from 'react-router-dom';
import { v4 as uuidv4 } from 'uuid';
import toast from 'react-hot-toast';
import format from 'date-fns/format';
import cn from 'classnames';
import { Grid, Typography, Button } from '@mui/material';
import DoneIcon from '@mui/icons-material/Done';
import CloseIcon from '@mui/icons-material/Close';
import DoDisturbIcon from '@mui/icons-material/DoDisturb';
import ReceiptIcon from '@mui/icons-material/ReceiptLongOutlined';
import ErrorOutlineIcon from '@mui/icons-material/ErrorOutline';
import { useCardano } from '@cardano-foundation/cardano-connect-with-wallet';
import { ROUTES } from 'common/routes';
import { EventTime } from 'components/EventTime/EventTime';
import {
  setIsConnectWalletModalVisible,
  setIsReceiptFetched,
  setIsVerifyVoteModalVisible,
  setIsVoteSubmittedModalVisible,
  setSelectedProposal,
  setVoteReceipt,
} from 'common/store/userSlice';
import { Account, SignedWeb3Request } from 'types/backend-services-types';
import { RootState } from 'common/store';
import { VoteReceipt } from 'pages/Vote/components/VoteReceipt/VoteReceipt';
import { Toast } from 'components/common/Toast/Toast';
import { VoteSubmittedModal } from 'components/VoteSubmittedModal/VoteSubmittedModal';
import { OptionCard } from '../../components/OptionCard/OptionCard';
import { OptionItem } from '../../components/OptionCard/OptionCard.types';
import SidePage from '../../components/common/SidePage/SidePage';
import { buildCanonicalVoteInputJson, buildCanonicalVoteReceiptInputJson } from '../../common/utils/voteUtils';
import * as voteService from '../../common/api/voteService';
import { env } from '../../env';
import { useToggle } from '../../common/hooks/useToggle';
import { HttpError } from '../../common/handlers/httpHandler';
import styles from './Vote.module.scss';
import { VerifyVoteModal } from './components/VerifyVote/VerifyVote';

const errorsMap = {
  INVALID_VOTING_POWER: 'To cast a vote, Voting Power should be more than 0',
  EXPIRED_SLOT: "CIP-93's envelope slot is expired!",
  VOTE_CANNOT_BE_CHANGED: 'You have already voted! Vote cannot be changed for this stake address',
};

const items: OptionItem[] = [
  {
    label: 'yes',
    icon: <DoneIcon sx={{ fontSize: { xs: '30px', md: '52px' }, color: '#39486C' }} />,
  },
  {
    label: 'no',
    icon: <CloseIcon sx={{ fontSize: { xs: '30px', md: '52px' }, color: '#39486C' }} />,
  },
  {
    label: 'abstain',
    icon: <DoDisturbIcon sx={{ fontSize: { xs: '30px', md: '52px' }, color: '#39486C' }} />,
  },
];

const Vote = () => {
  const { stakeAddress, isConnected, signMessage } = useCardano();
  const receipt = useSelector((state: RootState) => state.user.receipt);
  const event = useSelector((state: RootState) => state.user.event);
  const eventHasntStarted = !event?.active && !event?.finished;
  const isReceiptFetched = useSelector((state: RootState) => state.user.isReceiptFetched);
  const isVoteSubmittedModalVisible = useSelector((state: RootState) => state.user.isVoteSubmittedModalVisible);
  const isVerifyVoteModalVisible = useSelector((state: RootState) => state.user.isVerifyVoteModalVisible);
  const [absoluteSlot, setAbsoluteSlot] = useState<number>();
  const savedProposal = useSelector((state: RootState) => state.user.proposal);
  const [optionId, setOptionId] = useState(savedProposal || '');
  const [voteSubmitted, setVoteSubmitted] = useState(false);
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

  const fetchReceipt = useCallback(
    async (cb?: () => void) => {
      try {
        const voteObjectPayload = await getSignedMessagePromise(
          buildCanonicalVoteReceiptInputJson({
            voter: stakeAddress,
            slotNumber: absoluteSlot.toString(),
          })
        );

        const receiptResponse = await voteService.getVoteReceipt(voteObjectPayload);
        if ('id' in receiptResponse) {
          dispatch(setVoteReceipt({ receipt: receiptResponse }));
          dispatch(setSelectedProposal({ proposal: receiptResponse.proposal }));
        } else {
          const message = `Failed to fetch receipt', ${receiptResponse?.title}, ${receiptResponse?.detail}`;
          console.log(message);
          toast(
            <Toast
              message={message}
              icon={<ErrorOutlineIcon style={{ color: '#cc0e00' }} />}
            />
          );
        }
        dispatch(setIsReceiptFetched({ isFetched: true }));
        cb?.();
      } catch (error) {
        if (error?.message === 'VOTE_NOT_FOUND') {
          dispatch(setVoteReceipt({ receipt: null }));
          dispatch(setIsReceiptFetched({ isFetched: true }));
          return;
        }
        const message = `Failed to fetch receipt, ${error?.info || error?.message || error?.toString()}`;
        toast(
          <Toast
            message={message}
            icon={<ErrorOutlineIcon style={{ color: '#cc0e00' }} />}
          />
        );
        console.log(message);
      }
    },
    [absoluteSlot, dispatch, getSignedMessagePromise, stakeAddress]
  );

  const openReceiptDrawer = async () => {
    await fetchReceipt(toggleReceipt);
  };

  const init = useCallback(async () => {
    try {
      setAbsoluteSlot((await voteService.getSlotNumber())?.absoluteSlot);
    } catch (error) {
      const message = `Failed to fecth slot number: ${error?.message}`;
      console.log(message);
      toast(
        <Toast
          message={message}
          icon={<ErrorOutlineIcon style={{ color: '#cc0e00' }} />}
        />
      );
    }
  }, []);

  useEffect(() => {
    if (isConnected) {
      init();
    }
  }, [init, isConnected]);

  useEffect(() => {
    if (absoluteSlot && !savedProposal && !eventHasntStarted) {
      fetchReceipt();
    }
  }, [absoluteSlot, fetchReceipt, savedProposal, eventHasntStarted]);

  const onChangeOption = (option: string | null) => {
    setOptionId(option);
    if (!isConnected) dispatch(setIsConnectWalletModalVisible({ isVisible: true }));
  };

  const handleSubmit = async () => {
    if (!env.EVENT_ID) {
      console.log('EVENT_ID is not provided');
      return;
    }

    if (!isConnected) return;

    let votingPower: Account['votingPower'];
    try {
      ({ votingPower } = await voteService.getVotingPower(env.EVENT_ID, stakeAddress));
    } catch (error) {
      const message = `Failed to fetch votingPower ${
        error instanceof Error || error instanceof HttpError ? error?.message : error
      }`;

      console.log(message);
      toast(
        <Toast
          message={message}
          icon={<ErrorOutlineIcon style={{ color: '#cc0e00' }} />}
        />
      );
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
      setVoteSubmitted(true);
    } catch (error) {
      if (error instanceof HttpError && error.code === 400) {
        toast(
          <Toast
            message={errorsMap[error?.message as keyof typeof errorsMap] || error?.message}
            icon={<ErrorOutlineIcon style={{ color: '#cc0e00' }} />}
          />
        );
        setOptionId('');
      } else if (error instanceof Error) {
        toast(
          <Toast
            message={error?.message || error.toString()}
            icon={<ErrorOutlineIcon style={{ color: '#cc0e00' }} />}
          />
        );
        console.log('Failed to cast e vote', error);
      }
    }
  };

  const cantSelectOptions =
    !!receipt || voteSubmitted || (isConnected && !isReceiptFetched) || eventHasntStarted || event?.finished;
  const showViewReceiptButton = receipt?.id || voteSubmitted || (isConnected && event?.finished);
  const showConnectButton = !isConnected && !eventHasntStarted;
  const showSubmitButton = isConnected && !eventHasntStarted && !event?.finished && !showViewReceiptButton;

  return (
    <>
      <div className={styles.vote}>
        <Grid
          paddingTop={{ xs: '20px', md: '30px' }}
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
              fontSize={{
                xs: '28px',
                md: '56px',
              }}
              lineHeight={{
                xs: '33px',
                md: '65px',
              }}
            >
              CIP-1694 Vote
            </Typography>
          </Grid>
          <Grid item>
            <Typography marginBottom={{ xs: '38px', md: '24px' }}>
              <EventTime
                eventHasntStarted={eventHasntStarted}
                eventHasFinished={event?.finished}
                endTime={event?.eventEnd}
                startTime={event?.eventStart}
              />
            </Typography>
          </Grid>
          <Grid item>
            <Typography
              variant="h5"
              className={styles.description}
              lineHeight={{ xs: '19px', md: '36px' }}
              fontSize={{ xs: '16px', md: '28px' }}
            >
              Do you want CIP-1694 that will allow On-Chain Governance, implemented on the Cardano Blockchain?
            </Typography>
          </Grid>
          <Grid item>
            <OptionCard
              selectedOption={isConnected && savedProposal?.toLowerCase()}
              disabled={cantSelectOptions}
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
              <Grid
                justifyContent="center"
                alignItems="center"
                direction="column"
                container
                gap="12px"
                item
              >
                {showViewReceiptButton && (
                  <Button
                    className={cn(styles.button, styles.secondary)}
                    variant="contained"
                    onClick={() => openReceiptDrawer()}
                    aria-label="Receipt"
                    startIcon={<ReceiptIcon />}
                  >
                    Vote receipt
                  </Button>
                )}
                {showConnectButton && (
                  <Button
                    className={styles.button}
                    size="large"
                    variant="contained"
                    onClick={() => dispatch(setIsConnectWalletModalVisible({ isVisible: true }))}
                  >
                    {event?.finished ? 'Connect wallet to see your vote' : 'Connect wallet to vote'}
                  </Button>
                )}
                {showSubmitButton && (
                  <Button
                    className={cn(styles.button, {
                      [styles.disabled]: !optionId || !isReceiptFetched,
                    })}
                    size="large"
                    variant="contained"
                    disabled={!optionId || !isReceiptFetched}
                    onClick={() => handleSubmit()}
                  >
                    Submit Your Vote
                  </Button>
                )}
                {eventHasntStarted && (
                  <Button
                    className={cn(styles.button, { [styles.disabled]: true })}
                    size="large"
                    variant="contained"
                    disabled
                  >
                    Submit your vote from {event?.eventStart && format(new Date(event?.eventStart), 'do MMMM')}
                  </Button>
                )}
                {isConnected && event?.finished && (
                  <Button
                    className={styles.button}
                    size="large"
                    variant="contained"
                    component={Link}
                    to={ROUTES.LEADERBOARD}
                  >
                    View the results
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
          <VoteReceipt
            fetchReceipt={fetchReceipt}
            setOpen={toggleReceipt}
          />
        </SidePage>
      </div>
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
            Make sure to check back on <b>{event?.eventStart && format(new Date(event?.eventEnd), 'MMMM do ')}</b> to
            see the results!
          </>
        }
      />
      <VerifyVoteModal
        openStatus={isVerifyVoteModalVisible}
        onCloseFn={() => {
          dispatch(setIsVerifyVoteModalVisible({ isVisible: false }));
        }}
        name="vote-verify-modal"
        id="vote-verify-modal"
      />
    </>
  );
};

export default Vote;
