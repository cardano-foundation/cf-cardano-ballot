import React, { useCallback, useEffect, useState } from 'react';
import { Link, matchPath, useLocation, useNavigate } from 'react-router-dom';
import cn from 'classnames';
import { useDispatch, useSelector } from 'react-redux';
import toast from 'react-hot-toast';
import { Grid, Typography, Button } from '@mui/material';
import CheckBoxOutlinedIcon from '@mui/icons-material/CheckBoxOutlined';
import BlockIcon from '@mui/icons-material/Block';
import LeaderboardIcon from '@mui/icons-material/Leaderboard';
import { ROUTES } from 'common/routes';
import { useCardano } from '@cardano-foundation/cardano-connect-with-wallet';
import { RootState } from 'common/store';
import { getDateAndMonth } from 'common/utils/dateUtils';
import * as voteService from 'common/api/voteService';
import { setChainTipData } from 'common/store/userSlice';
import { Toast } from 'components/common/Toast/Toast';
import { ResultsCommingSoonModal } from 'pages/Leaderboard/components/ResultsCommingSoonModal/ResultsCommingSoonModal';
import { formatUTCDate } from 'pages/Leaderboard/utils';
import { ChainTip } from 'types/voting-ledger-follower-types';
import { ConnectWalletButton } from './ConnectWalletButton';
import styles from './HeaderActions.module.scss';

type HeaderActionsProps = {
  onClick?: () => void;
  isMobileMenu?: boolean;
  showNavigationItems?: boolean;
};

export const HeaderActions = ({ isMobileMenu = false, onClick, showNavigationItems }: HeaderActionsProps) => {
  const { isConnected } = useCardano();
  const location = useLocation();
  const navigate = useNavigate();
  const dispatch = useDispatch();

  const event = useSelector((state: RootState) => state.user.event);
  const tip = useSelector((state: RootState) => state.user.tip);
  const [isCommingSoonModalVisible, setIsCommingSoonModalVisible] = useState<boolean>(false);

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
    if (isConnected) {
      fetchChainTip();
    }
  }, [fetchChainTip, isConnected]);

  const goToLeaderboard = () => {
    navigate(ROUTES.LEADERBOARD);
    setIsCommingSoonModalVisible(false);
    onClick?.();
  };

  const onGoToLeaderboard = useCallback(async () => {
    const chainTip = await fetchChainTip();
    if (event.proposalsRevealEpoch > chainTip.epochNo) {
      setIsCommingSoonModalVisible(true);
    } else {
      navigate(ROUTES.LEADERBOARD);
    }
  }, [event?.proposalsRevealEpoch, fetchChainTip, navigate]);

  return (
    <>
      <Grid
        data-testid="header-actions"
        display="flex"
        item
        gap={{ xs: '10px', md: '15px' }}
        alignItems="center"
        justifyContent="flex-end"
        direction={{ xs: 'column', md: 'row' }}
        container
      >
        {showNavigationItems && (
          <>
            <Grid
              width={{ xs: '100% !important', md: 'auto !important' }}
              container
            >
              <Button
                data-testid="vote-link"
                onClick={onClick}
                component={Link}
                to={ROUTES.VOTE}
                className={cn(styles.button, { [styles.activeRoute]: !!matchPath(location?.pathname, ROUTES.VOTE) })}
                startIcon={<CheckBoxOutlinedIcon />}
                disabled={!event}
              >
                Your vote
              </Button>
            </Grid>
            <Grid
              width={{ xs: '100% !important', md: 'auto !important' }}
              container
            >
              <Button
                data-testid="leaderboard-link"
                onClick={() => onGoToLeaderboard()}
                sx={{ xs: { width: '100% !important' }, md: { width: 'auto !important' } }}
                className={cn(styles.button, {
                  [styles.activeRoute]: !!matchPath(location?.pathname, ROUTES.LEADERBOARD),
                })}
                startIcon={<LeaderboardIcon />}
                disabled={!event || !tip}
              >
                Leaderboard
              </Button>
            </Grid>
          </>
        )}
        <Typography
          className={cn(styles.walletButtonWrapper, { [styles.walletButtonWrapperMobile]: isMobileMenu })}
          variant="body2"
          color="text.secondary"
          align="center"
          component="div"
          marginLeft={{ xs: '0', md: '10px' }}
          width={{ xs: '100%', md: 'auto' }}
        >
          <ConnectWalletButton isMobileMenu={isMobileMenu} />
        </Typography>
      </Grid>
      <ResultsCommingSoonModal
        openStatus={isCommingSoonModalVisible}
        onCloseFn={() => {
          setIsCommingSoonModalVisible(false);
          onClick?.();
        }}
        onConfirmFn={() => goToLeaderboard()}
        onGoBackFn={() => {
          setIsCommingSoonModalVisible(false);
          onClick?.();
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
    </>
  );
};
