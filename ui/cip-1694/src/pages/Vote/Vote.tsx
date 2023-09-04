import React, { useCallback, useEffect, useMemo, useState } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { Link } from 'react-router-dom';
import { v4 as uuidv4 } from 'uuid';
import toast from 'react-hot-toast';
import cn from 'classnames';
import { Grid, Typography, Button, CircularProgress } from '@mui/material';
import DoneIcon from '@mui/icons-material/Done';
import CloseIcon from '@mui/icons-material/Close';
import DoDisturbIcon from '@mui/icons-material/DoDisturb';
import ReceiptIcon from '@mui/icons-material/ReceiptLongOutlined';
import BlockIcon from '@mui/icons-material/Block';
import { useCardano } from '@cardano-foundation/cardano-connect-with-wallet';
import { ROUTES } from 'common/routes';
import { EventTime } from 'components/EventTime/EventTime';
import {
  setIsConnectWalletModalVisible,
  setIsReceiptFetched,
  setIsVoteSubmittedModalVisible,
  setSelectedProposal,
  setVoteReceipt,
} from 'common/store/userSlice';
import { ProposalPresentation, Account } from 'types/voting-ledger-follower-types';
import { RootState } from 'common/store';
import { VoteReceipt } from 'pages/Vote/components/VoteReceipt/VoteReceipt';
import { Toast } from 'components/common/Toast/Toast';
import { VoteSubmittedModal } from 'components/VoteSubmittedModal/VoteSubmittedModal';
import { OptionCard } from 'components/OptionCard/OptionCard';
import { OptionItem } from 'components/OptionCard/OptionCard.types';
import SidePage from 'components/common/SidePage/SidePage';
import {
  buildCanonicalVoteInputJson,
  buildCanonicalVoteReceiptInputJson,
  getSignedMessagePromise,
} from 'common/utils/voteUtils';
import * as voteService from 'common/api/voteService';
import { useToggle } from 'common/hooks/useToggle';
import { HttpError } from 'common/handlers/httpHandler';
import { getDateAndMonth } from 'common/utils/dateUtils';
import { capitalize } from 'lodash';
import { env } from '../../env';
import styles from './Vote.module.scss';
import { ConfirmWithWalletSignatureModal } from './components/ConfirmWithWalletSignatureModal/ConfirmWithWalletSignatureModal';

const errorsMap = {
  INVALID_VOTING_POWER: 'To cast a vote, Voting Power should be more than 0',
  EXPIRED_SLOT: "CIP-93's envelope slot is expired!",
  VOTE_CANNOT_BE_CHANGED: 'You have already voted! Vote cannot be changed for this stake address',
};

const iconsMap: Record<ProposalPresentation['name'], React.ReactElement | null> = {
  YES: <DoneIcon sx={{ fontSize: { xs: '30px', md: '52px' }, color: '#39486C' }} />,
  NO: <CloseIcon sx={{ fontSize: { xs: '30px', md: '52px' }, color: '#39486C' }} />,
  ABSTAIN: <DoDisturbIcon sx={{ fontSize: { xs: '30px', md: '52px' }, color: '#39486C' }} />,
};

export const VotePage = () => {
  const { stakeAddress, isConnected, signMessage } = useCardano();
  const receipt = useSelector((state: RootState) => state.user.receipt);
  const event = useSelector((state: RootState) => state.user.event);
  const isReceiptFetched = useSelector((state: RootState) => state.user.isReceiptFetched);
  const isVoteSubmittedModalVisible = useSelector((state: RootState) => state.user.isVoteSubmittedModalVisible);
  const [absoluteSlot, setAbsoluteSlot] = useState<number>();
  const savedProposal = useSelector((state: RootState) => state.user.proposal);
  const [isReceiptDrawerInitializing, setIsReceiptDrawerInitializing] = useState(false);
  const [isCastingAVote, setIsCastingAVote] = useState(false);
  const [optionId, setOptionId] = useState(savedProposal || '');
  const [isConfirmWithWalletSignatureModalVisible, setIsConfirmWithWalletSignatureModalVisible] = useState(
    absoluteSlot && stakeAddress && !savedProposal && event?.notStarted === false
  );
  const [voteSubmitted, setVoteSubmitted] = useState(false);
  const [isToggledReceipt, toggleReceipt] = useToggle(false);
  const dispatch = useDispatch();

  useEffect(() => {
    if (absoluteSlot && stakeAddress && !savedProposal && event?.notStarted === false) {
      setIsConfirmWithWalletSignatureModalVisible(true);
    }
  }, [event?.notStarted, absoluteSlot, savedProposal, stakeAddress]);

  const items: OptionItem<ProposalPresentation['name']>[] = event?.categories
    ?.find(({ id }) => id === env.CATEGORY_ID)
    ?.proposals?.map(({ name }) => ({
      name,
      label: capitalize(name.toLowerCase()),
      icon: iconsMap[name] || null,
    }));

  const signMessagePromisified = useMemo(() => getSignedMessagePromise(signMessage), [signMessage]);

  const fetchReceipt = useCallback(
    async ({ cb, refetch = false }: { cb?: () => void; refetch?: boolean }) => {
      const errorPrefix = refetch
        ? 'Unable to refresh your vote receipt. Please try again'
        : 'Unable to fetch your vote receipt. Please try again';
      try {
        const voteObjectPayload = await signMessagePromisified(
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
          const message = `${errorPrefix}', ${receiptResponse?.title}, ${receiptResponse?.detail}`;
          console.log(message);
          toast(
            <Toast
              message={errorPrefix}
              error
              icon={<BlockIcon style={{ fontSize: '19px', color: '#F5F9FF' }} />}
            />
          );
        }
        dispatch(setIsReceiptFetched({ isFetched: true }));
        cb?.();
      } catch (error) {
        if (error?.message === 'VOTE_NOT_FOUND') {
          dispatch(setVoteReceipt({ receipt: null }));
          dispatch(setIsReceiptFetched({ isFetched: true }));
          setIsReceiptDrawerInitializing(false);
          setIsConfirmWithWalletSignatureModalVisible(false);
          return;
        }
        const message = `${errorPrefix}, ${error?.info || error?.message || error?.toString()}`;
        toast(
          <Toast
            message={errorPrefix}
            error
            icon={<BlockIcon style={{ fontSize: '19px', color: '#F5F9FF' }} />}
          />
        );
        console.log(message);
      }
      setIsReceiptDrawerInitializing(false);
      setIsConfirmWithWalletSignatureModalVisible(false);
    },
    [absoluteSlot, dispatch, signMessagePromisified, stakeAddress]
  );

  const openReceiptDrawer = async () => {
    setIsReceiptDrawerInitializing(true);
    await fetchReceipt({
      cb: () => {
        toggleReceipt();
        setIsReceiptDrawerInitializing(false);
      },
    });
  };

  const init = useCallback(async () => {
    try {
      setAbsoluteSlot((await voteService.getSlotNumber())?.absoluteSlot);
    } catch (error) {
      const message = `Failed to fecth slot number: ${error?.message}`;
      console.log(message);
      toast(
        <Toast
          message="Failed to fecth slot number"
          error
          icon={<BlockIcon style={{ fontSize: '19px', color: '#F5F9FF' }} />}
        />
      );
    }
  }, []);

  useEffect(() => {
    if (isConnected) {
      init();
    }
  }, [init, isConnected]);

  const onChangeOption = (option: string | null) => {
    setOptionId(option);
    if (!isConnected) dispatch(setIsConnectWalletModalVisible({ isVisible: true }));
  };

  const handleSubmit = async () => {
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
          error
          message="Unable to submit your vote. Please try again"
          icon={<BlockIcon style={{ fontSize: '19px', color: '#F5F9FF' }} />}
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
      setIsCastingAVote(true);
      const requestVoteObject = await signMessagePromisified(canonicalVoteInput);
      await voteService.castAVoteWithDigitalSignature(requestVoteObject);
      dispatch(setIsVoteSubmittedModalVisible({ isVisible: true }));
      setVoteSubmitted(true);
    } catch (error) {
      if (error instanceof HttpError && error.code === 400) {
        toast(
          <Toast
            error
            message="Unable to submit your vote. Please try again"
            icon={<BlockIcon style={{ fontSize: '19px', color: '#F5F9FF' }} />}
          />
        );
        setOptionId('');
        console.log('Failed to cast e vote', errorsMap[error?.message as keyof typeof errorsMap] || error?.message);
      } else if (error instanceof Error) {
        toast(
          <Toast
            error
            message="Unable to submit your vote. Please try again"
            icon={<BlockIcon style={{ fontSize: '19px', color: '#F5F9FF' }} />}
          />
        );
        console.log('Failed to cast e vote', error?.message || error.toString());
      }
    }
    setIsCastingAVote(true);
  };

  const cantSelectOptions =
    !!receipt || voteSubmitted || (isConnected && !isReceiptFetched) || event?.notStarted || event?.finished;
  const showViewReceiptButton = receipt?.id || voteSubmitted || (isReceiptFetched && event?.finished);
  const showConnectButton = !isConnected && !event?.notStarted;
  const showSubmitButton = isConnected && !event?.notStarted && !event?.finished && !showViewReceiptButton;

  return (
    <>
      <div
        className={styles.vote}
        data-testid="vote-page"
      >
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
              data-testid="event-title"
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
                eventHasntStarted={event?.notStarted}
                eventHasFinished={event?.finished}
                endTime={event?.eventEnd?.toString()}
                startTime={event?.eventStart?.toString()}
              />
            </Typography>
          </Grid>
          <Grid item>
            <Typography
              variant="h5"
              className={styles.description}
              lineHeight={{ xs: '19px', md: '36px' }}
              fontSize={{ xs: '16px', md: '28px' }}
              data-testid="event-description"
            >
              (..)
            </Typography>
          </Grid>
          <Grid item>
            <OptionCard
              selectedOption={isConnected && savedProposal}
              disabled={cantSelectOptions}
              items={items}
              onChangeOption={onChangeOption}
            />
          </Grid>
          <Grid item>
            <Grid
              container
              direction={{ xs: 'column', md: 'row' }}
              justifyContent={'center'}
              gap={{ xs: '0px', md: '51px' }}
              wrap="nowrap"
            >
              <Grid
                justifyContent="center"
                alignItems="center"
                direction="column"
                container
                gap="12px"
                item
                md={4}
                xs={12}
              />
              <Grid
                justifyContent="center"
                alignItems="center"
                direction="column"
                container
                gap="12px"
                item
                md={4}
                xs={12}
              >
                {showViewReceiptButton && (
                  <Button
                    className={cn(styles.button, styles.secondary)}
                    variant="contained"
                    onClick={() => openReceiptDrawer()}
                    aria-label="Receipt"
                    startIcon={<ReceiptIcon />}
                    data-testid="show-receipt-button"
                    disabled={isReceiptDrawerInitializing || !absoluteSlot}
                  >
                    Vote receipt
                    {isReceiptDrawerInitializing && (
                      <CircularProgress
                        size={20}
                        sx={{ marginLeft: '10px' }}
                      />
                    )}
                  </Button>
                )}
                {showConnectButton && (
                  <Button
                    className={styles.button}
                    size="large"
                    variant="contained"
                    onClick={() => dispatch(setIsConnectWalletModalVisible({ isVisible: true }))}
                    data-testid="proposal-connect-button"
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
                    disabled={!optionId || !isReceiptFetched || isCastingAVote || !absoluteSlot}
                    onClick={() => handleSubmit()}
                    data-testid="proposal-submit-button"
                  >
                    Submit your vote
                    {isCastingAVote && (
                      <CircularProgress
                        size={20}
                        sx={{ marginLeft: '10px' }}
                      />
                    )}
                  </Button>
                )}
                {event?.notStarted && (
                  <Button
                    className={cn(styles.button, { [styles.disabled]: true })}
                    size="large"
                    variant="contained"
                    disabled
                    data-testid="event-hasnt-started-submit-button"
                  >
                    Submit your vote from {event?.eventStart && getDateAndMonth(event?.eventStart?.toString())}
                  </Button>
                )}
                {isConnected && event?.finished && (
                  <Button
                    className={styles.button}
                    size="large"
                    variant="contained"
                    component={Link}
                    to={ROUTES.LEADERBOARD}
                    data-testid="view-results-button"
                  >
                    View the results
                  </Button>
                )}
              </Grid>
              <Grid
                justifyContent="center"
                alignItems="center"
                direction="column"
                container
                gap="12px"
                item
                md={4}
                xs={12}
              />
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
            Make sure to check back on <b>{event?.eventStart && getDateAndMonth(event?.eventEnd?.toString())}</b> to see
            the results!
          </>
        }
      />
      <ConfirmWithWalletSignatureModal
        openStatus={isConfirmWithWalletSignatureModalVisible}
        onConfirm={() => fetchReceipt({ cb: () => setIsConfirmWithWalletSignatureModalVisible(false) })}
        name="vote-submitted-modal"
        id="vote-submitted-modal"
        title="Wallet signature"
        description="We need to check if you’ve already voted. Please confirm with your wallet signature."
      />
    </>
  );
};
