import React, { useCallback, useEffect, useMemo, useState } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { Link } from 'react-router-dom';
import { v4 as uuidv4 } from 'uuid';
import capitalize from 'lodash/capitalize';
import findIndex from 'lodash/findIndex';
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
import { setIsConnectWalletModalVisible, setIsVoteSubmittedModalVisible } from 'common/store/userSlice';
import { ProposalPresentation, Account } from 'types/voting-ledger-follower-types';
import { VoteReceipt as VoteReceiptType } from 'types/voting-app-types';
import { RootState } from 'common/store';
import { VoteReceipt } from 'pages/Vote/components/VoteReceipt/VoteReceipt';
import { Toast } from 'components/common/Toast/Toast';
import { VoteSubmittedModal } from 'components/VoteSubmittedModal/VoteSubmittedModal';
import { OptionCard } from 'components/OptionCard/OptionCard';
import { OptionItem } from 'components/OptionCard/OptionCard.types';
import SidePage from 'components/common/SidePage/SidePage';
import { buildCanonicalVoteInputJson, getSignedMessagePromise } from 'common/utils/voteUtils';
import * as voteService from 'common/api/voteService';
import * as loginService from 'common/api/loginService';
import { useToggle } from 'common/hooks/useToggle';
import { HttpError } from 'common/handlers/httpHandler';
import { getDateAndMonth } from 'common/utils/dateUtils';
import { getUserInSession, saveUserInSession, tokenIsExpired } from 'common/utils/session';
import { ConfirmWithWalletSignatureModal } from './components/ConfirmWithWalletSignatureModal/ConfirmWithWalletSignatureModal';
import { env } from '../../env';
import styles from './Vote.module.scss';

const copies = [
  {
    title: 'The Governance of Cardano',
    body: 'Should Cardano change its governance structure?',
  },
  {
    title: 'The Governance of Cardano',
    body: 'Should Cardano implement the minimum-viable governance proposed in CIP-1694?',
  },
];

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
  const [receipt, setReceipt] = useState<VoteReceiptType | null>(null);
  const event = useSelector((state: RootState) => state.user.event);
  const tip = useSelector((state: RootState) => state.user.tip);
  const [isReceiptFetched, setIsReceiptFetched] = useState(false);
  const isVoteSubmittedModalVisible = useSelector((state: RootState) => state.user.isVoteSubmittedModalVisible);
  const [isReceiptDrawerInitializing, setIsReceiptDrawerInitializing] = useState(false);
  const [isCastingAVote, setIsCastingAVote] = useState(false);
  const [optionId, setOptionId] = useState('');
  const [isConfirmWithWalletSignatureModalVisible, setIsConfirmWithWalletSignatureModalVisible] = useState(false);
  const [voteSubmitted, setVoteSubmitted] = useState(false);
  const [category, setCategory] = useState(event?.categories?.[0].id);
  const [isToggledReceipt, toggleReceipt] = useToggle(false);
  const dispatch = useDispatch();

  useEffect(() => {
    setCategory(event?.categories?.[0].id);
  }, [event]);

  useEffect(() => {
    const session = getUserInSession();

    if (
      tip?.absoluteSlot &&
      stakeAddress &&
      event?.notStarted === false &&
      ((session && tokenIsExpired(session.expiresAt)) || !session)
    ) {
      setIsConfirmWithWalletSignatureModalVisible(true);
    }
  }, [event?.notStarted, tip?.absoluteSlot, stakeAddress]);

  const items: OptionItem<ProposalPresentation['name']>[] = event?.categories
    ?.find(({ id }) => id === category)
    ?.proposals?.map(({ name }) => ({
      name,
      label: capitalize(name.toLowerCase()),
      icon: iconsMap[name] || null,
    }));

  const signMessagePromisified = useMemo(() => getSignedMessagePromise(signMessage), [signMessage]);

  const login = useCallback(async () => {
    const canonicalVoteInput = loginService.buildCanonicalLoginJson({
      stakeAddress,
      slotNumber: tip.absoluteSlot.toString(),
    });
    try {
      const requestVoteObject = await signMessagePromisified(canonicalVoteInput);
      const response = await loginService.submitLogin(requestVoteObject);
      const session = {
        accessToken: response.accessToken,
        expiresAt: response.expiresAt,
      };
      saveUserInSession(session);
      return session?.accessToken;
    } catch (error) {
      const message = `${error?.info || error?.message || error?.toString()}`;
      toast(
        <Toast
          message={message}
          error
          icon={<BlockIcon style={{ fontSize: '19px', color: '#F5F9FF' }} />}
        />
      );
      console.log(message);
    }
  }, [signMessagePromisified, stakeAddress, tip?.absoluteSlot]);

  const fetchReceipt = useCallback(
    async ({ cb, refetch = false }: { cb?: () => void; refetch?: boolean }) => {
      const errorPrefix = refetch
        ? 'Unable to refresh your vote receipt. Please try again'
        : 'Unable to fetch your vote receipt. Please try again';
      try {
        const session = getUserInSession();
        let token = session?.accessToken;
        if (!session || tokenIsExpired(session?.expiresAt)) {
          token = await login();
        }
        if (!token) return;
        const receiptResponse = await voteService.getVoteReceipt(category, token);

        if ('id' in receiptResponse) {
          setReceipt(receiptResponse);
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
        setIsReceiptFetched(true);
        cb?.();
      } catch (error) {
        if (error?.message === 'VOTE_NOT_FOUND') {
          setReceipt(null);
          setIsReceiptFetched(true);
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
    [login, category]
  );

  useEffect(() => {
    const session = getUserInSession();
    if (isConnected && tip?.absoluteSlot && stakeAddress && session && !tokenIsExpired(session.expiresAt) && category) {
      fetchReceipt({});
    }
  }, [fetchReceipt, isConnected, stakeAddress, tip?.absoluteSlot, category]);

  const openReceiptDrawer = async () => {
    setIsReceiptDrawerInitializing(true);
    await fetchReceipt({
      cb: () => {
        toggleReceipt();
        setIsReceiptDrawerInitializing(false);
      },
    });
  };

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
      slotNumber: tip.absoluteSlot.toString(),
      votePower: votingPower,
      category,
    });

    try {
      setIsCastingAVote(true);
      const requestVoteObject = await signMessagePromisified(canonicalVoteInput);
      await voteService.castAVoteWithDigitalSignature(requestVoteObject);
      dispatch(setIsVoteSubmittedModalVisible({ isVisible: true }));
      setVoteSubmitted(true);
      await fetchReceipt({});
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

  const onChangeCategory = () => {
    setIsReceiptFetched(false);
    const currentCategoryIndex = findIndex(event?.categories, ['id', category]);
    setCategory(event?.categories[(currentCategoryIndex + 1) % event?.categories?.length]?.id);
  };

  const cantSelectOptions =
    !!receipt || voteSubmitted || (isConnected && !isReceiptFetched) || event?.notStarted || event?.finished;
  const showViewReceiptButton = receipt?.id || voteSubmitted || (isReceiptFetched && event?.finished);
  const showConnectButton = !isConnected && !event?.notStarted;
  const showSubmitButton = isConnected && !event?.notStarted && !event?.finished && !showViewReceiptButton;
  const showPagination = isConnected && receipt && category === receipt?.category && event?.categories?.length > 1;

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
              {copies[findIndex(event?.categories, ['id', category])]?.title}
            </Typography>
          </Grid>
          <Grid item>
            <Typography marginBottom={{ xs: '38px', md: '24px' }}>
              <EventTime
                eventHasntStarted={event?.notStarted}
                eventHasFinished={event?.finished}
                endTime={event?.eventEndDate?.toString()}
                startTime={event?.eventStartDate?.toString()}
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
              {copies[findIndex(event?.categories, ['id', category])]?.body}
            </Typography>
          </Grid>
          <Grid item>
            <OptionCard
              selectedOption={isConnected && receipt?.proposal}
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
                    disabled={isReceiptDrawerInitializing || !tip?.absoluteSlot}
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
                    disabled={!optionId || !isReceiptFetched || isCastingAVote || !tip?.absoluteSlot}
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
                    Submit your vote from {event?.eventStartDate && getDateAndMonth(event?.eventStartDate?.toString())}
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
                {showPagination && (
                  <Button
                    onClick={() => onChangeCategory()}
                    className={styles.button}
                    size="large"
                    variant="contained"
                    data-testid="next-question-button"
                  >
                    Next question
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
            receipt={receipt}
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
            Make sure to check back on{' '}
            <b>{event?.eventStartDate && getDateAndMonth(event?.eventEndDate?.toString())}</b> to see the results!
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
