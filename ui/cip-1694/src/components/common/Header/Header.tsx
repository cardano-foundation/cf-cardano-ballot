import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useSelector } from 'react-redux';
import { Grid, Button } from '@mui/material';
import MenuIcon from '@mui/icons-material/Menu';
import { RootState } from 'common/store';
import { HeaderActions } from './components/HeaderActions';
import { MobileModal } from '../MobileModal/MobileModal';
import { Footer } from '../Footer/Footer';
import styles from './Header.module.scss';

export const Header = () => {
  const navigate = useNavigate();
  const event = useSelector((state: RootState) => state.user.event);
  const eventHasntStarted = !event?.active && !event?.finished;

  const [isMobileMenuVisible, setIsMobileMenuVisible] = useState(false);

  const handleLogoClick = () => {
    navigate('/');
  };

  return (
    <>
      <Grid
        container
        direction={{ xs: 'column', md: 'row' }}
        justifyContent={{ xs: 'center', sm: 'space-between' }}
        className={styles.container}
        alignContent={{ xs: 'space-between', sm: 'center' }}
        justifySelf={{
          xs: 'flex-start',
        }}
        height={{ xs: '69px', sm: 'auto', md: '69px' }}
        marginBottom={{ sm: '40px', md: '0px' }}
      >
        <Grid
          item
          xs={12}
          sm="auto"
          display="flex"
          className={styles.content}
          alignItems="center"
          marginBottom={{ xs: '0px', sm: '15px', md: '0px' }}
          marginTop={{ xs: '0px', sm: '15px', md: '0px' }}
        >
          <span
            onClick={handleLogoClick}
            className={styles.logo}
          >
            CIP-1694 Ratification
          </span>
        </Grid>
        <Grid display={{ xs: 'none', sm: 'flex' }}>
          <HeaderActions
            showNavigationItems={!eventHasntStarted}
            hideLeaderboard={!event?.finished}
          />
        </Grid>
        <Grid
          display={{ xs: 'block', sm: 'none' }}
          item
          sm="auto"
          gap="15px"
          alignItems="center"
          justifyContent="flex-end"
        >
          <Button
            className={styles.menuButton}
            size="large"
            variant="outlined"
            onClick={() => setIsMobileMenuVisible(true)}
          >
            <MenuIcon className={styles.menuIcon} />
          </Button>
        </Grid>
      </Grid>
      <MobileModal
        openStatus={isMobileMenuVisible}
        onCloseFn={() => setIsMobileMenuVisible(false)}
        name="mobile-menu"
        id="mobile-menu"
        title="Menu"
      >
        <Grid
          container
          flex="1"
          direction="column"
          justifyContent="space-between"
        >
          <HeaderActions
            hideLeaderboard={!event?.finished}
            showNavigationItems={!eventHasntStarted}
            onClick={() => setIsMobileMenuVisible(false)}
            isMobileMenu
          />
          <Footer isMobileMenu />
        </Grid>
      </MobileModal>
    </>
  );
};
