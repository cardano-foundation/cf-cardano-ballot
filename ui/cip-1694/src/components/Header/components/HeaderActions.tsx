import React, { useCallback, useEffect } from 'react';
import { Link, matchPath, useLocation, useNavigate } from 'react-router-dom';
import cn from 'classnames';
import { useDispatch, useSelector } from 'react-redux';
import toast from 'react-hot-toast';
import { Grid, Typography, Button } from '@mui/material';
import CheckBoxOutlinedIcon from '@mui/icons-material/CheckBoxOutlined';
import BlockIcon from '@mui/icons-material/Block';
import LeaderboardIcon from '@mui/icons-material/Leaderboard';
import { ROUTES } from 'common/routes';
import { RootState } from 'common/store';
import * as voteService from 'common/api/voteService';
import {
  setChainTipData,
  setIsCommingSoonModalVisible,
  setIsMobileMenuVisible as setIsMobileMenuVisibleAction,
} from 'common/store/userSlice';
import { Toast } from 'components/Toast/Toast';
import { ChainTip } from 'types/voting-ledger-follower-types';
import { ConnectWalletButton } from './ConnectWalletButton';
import styles from './HeaderActions.module.scss';

type HeaderActionsProps = {
  isMobileMenu?: boolean;
};

export const HeaderActions = ({ isMobileMenu = false }: HeaderActionsProps) => {
  const location = useLocation();
  const navigate = useNavigate();
  const dispatch = useDispatch();
  const setIsMobileMenuVisible = useCallback(
    (isVisible: boolean) => {
      dispatch(setIsMobileMenuVisibleAction({ isVisible }));
    },
    [dispatch]
  );

  const event = useSelector((state: RootState) => state.user.event);
  const tip = useSelector((state: RootState) => state.user.tip);

  const fetchChainTip = useCallback(async () => {
    let chainTip: ChainTip = null;
    try {
      chainTip = await voteService.getChainTip();
      dispatch(setChainTipData({ tip: chainTip }));
    } catch (error) {
      toast(
        <Toast
          message="Failed to fetch chain tip"
          error
          icon={<BlockIcon style={{ fontSize: '19px', color: '#F5F9FF' }} />}
        />
      );
    }
    return chainTip;
  }, [dispatch]);

  useEffect(() => {
    fetchChainTip();
  }, [fetchChainTip]);

  const onGoToLeaderboard = useCallback(async () => {
    const chainTip = await fetchChainTip();
    if (event?.proposalsRevealEpoch > chainTip?.epochNo) {
      dispatch(setIsCommingSoonModalVisible({ isVisible: true }));
    } else {
      navigate(ROUTES.LEADERBOARD);
    }
  }, [dispatch, event?.proposalsRevealEpoch, fetchChainTip, navigate]);

  return (
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
      <>
        <Grid
          width={{ xs: '100% !important', md: 'auto !important' }}
          container
        >
          <Button
            data-testid="vote-link"
            onClick={() => setIsMobileMenuVisible(false)}
            component={Link}
            to={ROUTES.VOTE}
            className={cn(styles.button, { [styles.activeRoute]: !!matchPath(location?.pathname, ROUTES.VOTE) })}
            startIcon={<CheckBoxOutlinedIcon />}
            disabled={!event}
          >
            Your Ballot
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
  );
};
