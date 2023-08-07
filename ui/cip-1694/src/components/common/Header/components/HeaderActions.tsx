import React from 'react';
import { Link, matchPath, useLocation } from 'react-router-dom';
import cn from 'classnames';
import { useCardano } from '@cardano-foundation/cardano-connect-with-wallet';
import { Grid, Typography, Button } from '@mui/material';
import CheckBoxOutlinedIcon from '@mui/icons-material/CheckBoxOutlined';
import LeaderboardIcon from '@mui/icons-material/Leaderboard';
import { ROUTES } from 'common/routes';
import { ConnectWalletButton } from './ConnectWalletButton';
import styles from './HeaderActions.module.scss';

type HeaderActionsProps = {
  onClick?: () => void;
  isMobileMenu?: boolean;
  showNavigationItems?: boolean;
  hideLeaderboard?: boolean;
};

export const HeaderActions = ({
  isMobileMenu = false,
  onClick,
  showNavigationItems,
  hideLeaderboard,
}: HeaderActionsProps) => {
  const { isConnected } = useCardano();
  const { pathname } = useLocation();

  return (
    <Grid
      display="flex"
      item
      gap={{ xs: '10px', sm: '15px' }}
      alignItems="center"
      justifyContent="flex-end"
      direction={{ xs: 'column', md: 'row' }}
      container
    >
      {showNavigationItems && (
        <>
          <Grid
            width={{ xs: '100% !important', sm: 'auto !important' }}
            container
          >
            <Button
              onClick={onClick}
              component={Link}
              to={ROUTES.VOTE}
              className={cn(styles.button, { [styles.activeRoute]: !!matchPath(pathname, ROUTES.VOTE) })}
              startIcon={<CheckBoxOutlinedIcon />}
            >
              Your vote
            </Button>
          </Grid>
          {!hideLeaderboard && (
            <Grid
              width={{ xs: '100% !important', sm: 'auto !important' }}
              container
            >
              <Button
                onClick={onClick}
                sx={{ xs: { width: '100% !important' }, sm: { width: 'auto !important' } }}
                component={Link}
                to={isConnected ? ROUTES.LEADERBOARD : undefined}
                className={cn(styles.button, { [styles.activeRoute]: !!matchPath(pathname, ROUTES.LEADERBOARD) })}
                startIcon={<LeaderboardIcon />}
              >
                Leaderboard
              </Button>
            </Grid>
          )}
        </>
      )}
      <Typography
        className={cn(styles.walletButtonWrapper, { [styles.walletButtonWrapperMobile]: isMobileMenu })}
        variant="body2"
        color="text.secondary"
        align="center"
        component="div"
        marginLeft={{ xs: '0', md: '10px' }}
        width={{ xs: '100%', sm: 'auto' }}
      >
        <ConnectWalletButton isMobileMenu={isMobileMenu} />
      </Typography>
    </Grid>
  );
};
