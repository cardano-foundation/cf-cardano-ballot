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
import ArrowBack from '@mui/icons-material/ArrowBack';
import ArrowForward from '@mui/icons-material/ArrowForward';
import BlockIcon from '@mui/icons-material/Block';
import { useCardano } from '@cardano-foundation/cardano-connect-with-wallet';
import { ROUTES } from 'common/routes';
import { EventTime } from 'components/EventTime/EventTime';
import {
  setIsConnectWalletModalVisible,
  setIsVoteSubmittedModalVisible,
  setChainTipData,
} from 'common/store/userSlice';
import { ProposalPresentation, Account, ChainTip } from 'types/voting-ledger-follower-types';
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
import { resolveCardanoNetwork } from 'common/utils/common';
import { getUserInSession, saveUserInSession, tokenIsExpired } from 'common/utils/session';
import { ConfirmWithWalletSignatureModal } from './components/ConfirmWithWalletSignatureModal/ConfirmWithWalletSignatureModal';
import { VoteContextInput } from './components/VoteContextInput/VoteContextInput';
import { env } from '../../env';
import styles from './Vote.module.scss';

export const errorsMap = {
  [voteService.ERRORS.STAKE_AMOUNT_NOT_AVAILABLE]: (stakeAddress: string) => (
    <div>
      <div>Stake amount not found for stake address:</div>
      <div style={{ fontSize: '14px' }}>${stakeAddress}</div>
    </div>
  ),
  [voteService.ERRORS.VOTE_CANNOT_BE_CHANGED]: (stakeAddress: string) => (
    <div>
      <div>Vote cannot be changed for the stake address:</div>
      <div style={{ fontSize: '14px' }}>${stakeAddress}</div>
    </div>
  ),
};

const copies = [
  {
    title: 'The Governance of Cardano',
    body: 'Do you like pineapple pizza?',
  },
  {
    title: 'The Governance of Cardano',
    body: 'Do you like apples?',
  },
];

const iconsMap: Record<ProposalPresentation['name'], React.ReactElement | null> = {
  YES: <DoneIcon sx={{ fontSize: { xs: '30px', md: '52px' }, color: '#39486C' }} />,
  NO: <CloseIcon sx={{ fontSize: { xs: '30px', md: '52px' }, color: '#39486C' }} />,
  ABSTAIN: <DoDisturbIcon sx={{ fontSize: { xs: '30px', md: '52px' }, color: '#39486C' }} />,
};

export const VotePage = () => {
  const { stakeAddress, isConnected, signMessage } = useCardano({
    limitNetwork: resolveCardanoNetwork(env.TARGET_NETWORK),
  });
  const [receipt, setReceipt] = useState<VoteReceiptType | null>(null);
  const [voteContext, setVoteContext] = useState('');
  const event = useSelector((state: RootState) => state.user.event);
  const tip = useSelector((state: RootState) => state.user.tip);
  const [isReceiptFetched, setIsReceiptFetched] = useState(false);
  const isVoteSubmittedModalVisible = useSelector((state: RootState) => state.user.isVoteSubmittedModalVisible);
  const [isReceiptDrawerInitializing, setIsReceiptDrawerInitializing] = useState(false);
  const [isCastingAVote, setIsCastingAVote] = useState(false);
  const [optionId, setOptionId] = useState<string | null>();
  const [isConfirmWithWalletSignatureModalVisible, setIsConfirmWithWalletSignatureModalVisible] = useState(false);
  const [voteSubmitted, setVoteSubmitted] = useState(false);
  const [activeCategoryId, setActiveCategoryId] = useState(event?.categories?.[0].id);
  const numOfCategories = event?.categories?.length;
  const activeCategoryIndex = findIndex(event?.categories, ['id', activeCategoryId]);
  const [isToggledReceipt, toggleReceipt] = useToggle(false);
  const couldAddContext = activeCategoryIndex === 0 && isReceiptFetched && !receipt;
  const dispatch = useDispatch();

  const fetchChainTip = useCallback(async () => {
    let chainTip: ChainTip = null;
    try {
      chainTip = await voteService.getChainTip();
      dispatch(setChainTipData({ tip: chainTip }));
    } catch (error) {
      toast(
        <Toast
          message="Failed to fecth chain tip"
          error
          icon={<BlockIcon style={{ fontSize: '19px', color: '#F5F9FF' }} />}
        />
      );
    }
    return chainTip;
  }, [dispatch]);

  useEffect(() => {
    setActiveCategoryId(event?.categories?.[0].id);
  }, [event]);

  useEffect(() => {
    const session = getUserInSession();

    if (stakeAddress && event?.notStarted === false && ((session && tokenIsExpired(session.expiresAt)) || !session)) {
      setIsConfirmWithWalletSignatureModalVisible(true);
    }
  }, [event?.notStarted, stakeAddress]);

  const items: OptionItem<ProposalPresentation['name']>[] = event?.categories
    ?.find(({ id }) => id === activeCategoryId)
    ?.proposals?.map(({ name }) => ({
      id: `${activeCategoryId}-${name}`,
      name,
      label: capitalize(name.toLowerCase()),
      icon: iconsMap[name] || null,
    }));

  const signMessagePromisified = useMemo(() => getSignedMessagePromise(signMessage), [signMessage]);

  const login = useCallback(async () => {
    try {
      const chainTip = await fetchChainTip();
      const canonicalVoteInput = loginService.buildCanonicalLoginJson({
        stakeAddress,
        slotNumber: chainTip.absoluteSlot.toString(),
      });
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
    }
  }, [fetchChainTip, signMessagePromisified, stakeAddress]);

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
        const receiptResponse = await voteService.getVoteReceipt(activeCategoryId, token);

        if ('id' in receiptResponse) {
          setReceipt(receiptResponse);
        } else {
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
        toast(
          <Toast
            message={errorPrefix}
            error
            icon={<BlockIcon style={{ fontSize: '19px', color: '#F5F9FF' }} />}
          />
        );
      }
      setIsReceiptDrawerInitializing(false);
      setIsConfirmWithWalletSignatureModalVisible(false);
    },
    [login, activeCategoryId]
  );

  useEffect(() => {
    const session = getUserInSession();
    if (isConnected && stakeAddress && session && !tokenIsExpired(session.expiresAt) && activeCategoryId) {
      fetchReceipt({});
    }
  }, [fetchReceipt, isConnected, stakeAddress, activeCategoryId]);

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
    if (!isConnected && option) dispatch(setIsConnectWalletModalVisible({ isVisible: true }));
  };

  const onChangeCategory = (categoryIndex: number) => {
    setReceipt(null);
    setOptionId(null);
    setVoteSubmitted(false);
    setIsReceiptFetched(false);
    setActiveCategoryId(event?.categories[categoryIndex]?.id);
  };

  const submitVoteContextForm = useCallback(async () => {
    try {
      await voteService.submitVoteContextForm({
        [env.GOOGLE_FORM_VOTE_CONTEXT_INPUT_NAME]: voteContext,
      });
    } catch (error) {
      console.log(error?.message || error);
    } finally {
      setVoteContext('');
    }
  }, [voteContext]);

  const handleSubmit = async () => {
    let votingPower: Account['votingPower'];
    try {
      ({ votingPower } = await voteService.getVotingPower(env.EVENT_ID, stakeAddress));
    } catch (error) {
      toast(
        <Toast
          error
          message={errorsMap[error?.message]?.(stakeAddress) || 'Unable to submit your vote. Please try again'}
          icon={<BlockIcon style={{ fontSize: '19px', color: '#F5F9FF' }} />}
        />
      );
    }

    if (!votingPower) return;

    try {
      const chainTip = await fetchChainTip();

      const canonicalVoteInput = buildCanonicalVoteInputJson({
        option: optionId?.toUpperCase(),
        voter: stakeAddress,
        voteId: uuidv4(),
        slotNumber: chainTip.absoluteSlot.toString(),
        votingPower,
        category: activeCategoryId,
      });
      setIsCastingAVote(true);
      const requestVoteObject = await signMessagePromisified(canonicalVoteInput);
      await voteService.castAVoteWithDigitalSignature(requestVoteObject);
      dispatch(setIsVoteSubmittedModalVisible({ isVisible: true }));
      if (couldAddContext && voteContext) {
        await submitVoteContextForm();
      }
      setVoteSubmitted(true);
      if (numOfCategories === 1 || activeCategoryIndex === numOfCategories - 1) {
        await fetchReceipt({});
      } else {
        onChangeCategory(activeCategoryIndex + 1);
      }
    } catch (error) {
      toast(
        <Toast
          error
          message={errorsMap[error?.message]?.(stakeAddress) || 'Unable to submit your vote. Please try again'}
          icon={<BlockIcon style={{ fontSize: '19px', color: '#F5F9FF' }} />}
        />
      );
      if (error instanceof HttpError && error.code === 400) {
        setOptionId('');
      }

      if (error.message === 'VOTE_CANNOT_BE_CHANGED') {
        setOptionId('');
        setIsConfirmWithWalletSignatureModalVisible(true);
      }
    }
    setIsCastingAVote(false);
  };

  const onRefetchSuccess = useCallback(() => {
    toast(<Toast message="Receipt has been successfully refreshed" />);
  }, []);

  const cantSelectOptions =
    !!receipt || voteSubmitted || (isConnected && !isReceiptFetched) || event?.notStarted || event?.finished;
  const showViewReceiptButton = receipt?.id || voteSubmitted || (isReceiptFetched && event?.finished);
  const showConnectButton = event && !isConnected && event?.notStarted === false;
  const showSubmitButton =
    event && isConnected && event?.notStarted === false && event?.finished === false && !showViewReceiptButton;
  const showPagination = isConnected && receipt && activeCategoryId === receipt?.category && numOfCategories > 1;

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
              {copies[activeCategoryIndex]?.title}
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
          <Grid
            display="flex"
            justifyContent="space-between"
            alignItems="center"
            item
          >
            <Typography
              variant="h5"
              className={styles.description}
              lineHeight={{ xs: '19px', md: '36px' }}
              fontSize={{ xs: '16px', md: '28px' }}
              data-testid="event-description"
            >
              {copies[activeCategoryIndex]?.body}
            </Typography>
            {numOfCategories > 1 && (
              <Typography
                variant="h5"
                className={styles.categoryPagination}
                data-testid="category-pagination"
                lineHeight={{ xs: '16px', md: '22px' }}
                fontSize={{ xs: '16px', md: '22px' }}
              >
                Question {activeCategoryIndex + 1} of {numOfCategories}
              </Typography>
            )}
          </Grid>
          <Grid item>
            <OptionCard
              key={activeCategoryId}
              selectedOption={(isConnected && receipt?.proposal) || optionId}
              disabled={cantSelectOptions}
              items={items}
              onChangeOption={onChangeOption}
            />
          </Grid>
          {couldAddContext && (
            <Grid item>
              <VoteContextInput
                disabled={!optionId}
                onChange={setVoteContext}
                voteContext={voteContext}
              />
            </Grid>
          )}
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
                    startIcon={activeCategoryIndex === 1 && <ArrowBack style={{ fontSize: '20px', margin: '0' }} />}
                    endIcon={activeCategoryIndex === 0 && <ArrowForward style={{ fontSize: '20px', margin: '0' }} />}
                    onClick={() => onChangeCategory(activeCategoryIndex === 0 ? 1 : 0)}
                    sx={{ gap: '10px' }}
                    className={styles.button}
                    size="large"
                    variant="contained"
                    data-testid="next-question-button"
                  >
                    {activeCategoryIndex === 0 ? 'Next question' : 'Previous question'}
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
            fetchReceipt={() => fetchReceipt({ refetch: true, cb: onRefetchSuccess })}
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
        onCloseFn={() => {
          setIsReceiptFetched(true);
          setIsConfirmWithWalletSignatureModalVisible(false);
        }}
        name="vote-submitted-modal"
        id="vote-submitted-modal"
        title="Wallet signature"
        description={
          <>
            We need to check if you’ve already voted.
            <br />
            You will see a pop-up message from your wallet.
            <br />
            Please confirm with your wallet signature.
          </>
        }
      />
    </>
  );
};
