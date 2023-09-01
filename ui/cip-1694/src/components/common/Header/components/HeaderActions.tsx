import React, { useState } from 'react';
import { Link, matchPath, useLocation, useNavigate } from 'react-router-dom';
import cn from 'classnames';
import { Grid, Typography, Button } from '@mui/material';
import CheckBoxOutlinedIcon from '@mui/icons-material/CheckBoxOutlined';
import LeaderboardIcon from '@mui/icons-material/Leaderboard';
import { ROUTES } from 'common/routes';
import { ResultsCommingSoonModal } from 'pages/Leaderboard/components/ResultsCommingSoonModal/ResultsCommingSoonModal';
import { useSelector } from 'react-redux';
import { RootState } from 'common/store';
import { formatUTCDate } from 'pages/Leaderboard/utils';
import { getDateAndMonth } from 'common/utils/dateUtils';
import { ConnectWalletButton } from './ConnectWalletButton';
import styles from './HeaderActions.module.scss';

type HeaderActionsProps = {
  onClick?: () => void;
  isMobileMenu?: boolean;
  showNavigationItems?: boolean;
};

export const HeaderActions = ({ isMobileMenu = false, onClick, showNavigationItems }: HeaderActionsProps) => {
  const location = useLocation();
  const navigate = useNavigate();

  const event = useSelector((state: RootState) => state.user.event);
  const [isCommingSoonModalVisible, setIsCommingSoonModalVisible] = useState<boolean>(false);

  const goToLeaderboard = () => {
    navigate(ROUTES.LEADERBOARD);
    setIsCommingSoonModalVisible(false);
    onClick?.();
  };

  const onGoToLeaderboard = () => {
    if (event?.finished === false) {
      setIsCommingSoonModalVisible(true);
    } else {
      navigate(ROUTES.LEADERBOARD);
    }
  };

  return (
    <>
      <Grid
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
                disabled={!event}
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
        onCloseFn={() => goToLeaderboard()}
        onGoBackFn={() => {
          setIsCommingSoonModalVisible(false);
          onClick?.();
        }}
        name="vote-submitted-modal"
        id="vote-submitted-modal"
        title="Coming soon"
        description={
          <>
            The results will be displayed after the voting has closed on{' '}
            <b>
              {event?.eventStart && getDateAndMonth(event?.eventEnd?.toString())}{' '}
              {formatUTCDate(event?.eventEnd?.toString())}
            </b>
          </>
        }
      />
    </>
  );
};
