import React from 'react';
import { Link, matchPath, useLocation, useNavigate } from 'react-router-dom';
import cn from 'classnames';
import { Grid, Typography, Button } from '@mui/material';
import CheckBoxOutlinedIcon from '@mui/icons-material/CheckBoxOutlined';
import LeaderboardIcon from '@mui/icons-material/Leaderboard';
import { ROUTES } from 'common/routes';
import { ConnectWalletButton } from './ConnectWalletButton';
import styles from './Header.module.scss';

export const Header = () => {
  const navigate = useNavigate();
  const { pathname } = useLocation();

  const handleLogoClick = () => {
    navigate('/');
  };

  return (
    <Grid
      container
      direction={{ xs: 'column', sm: 'row' }}
      justifyContent={{ sm: 'center', md: 'space-between' }}
      alignItems="center"
      className={styles.container}
    >
      <Grid
        item
        xs={12}
        sm={'auto'}
        className={styles.content}
      >
        <img
          className={styles.logo}
          src="/static/Cardano_Ballot_black.png"
          onClick={handleLogoClick}
        />
      </Grid>
      <Grid
        item
        xs={12}
        sm={'auto'}
        className={styles.content}
        gap={'15px'}
        justifyContent={'flex-end'}
      >
        <Button
          component={Link}
          to={ROUTES.VOTE}
          className={cn(styles.button, { [styles.activeRoute]: !!matchPath(pathname, ROUTES.VOTE) })}
          startIcon={<CheckBoxOutlinedIcon />}
        >
          Your vote
        </Button>
        <Button
          className={styles.button}
          startIcon={<LeaderboardIcon />}
        >
          Leaderboard
        </Button>
        <Typography
          variant="body2"
          color="text.secondary"
          align="center"
          component={'div'}
        >
          <ConnectWalletButton />
        </Typography>
      </Grid>
    </Grid>
  );
};
