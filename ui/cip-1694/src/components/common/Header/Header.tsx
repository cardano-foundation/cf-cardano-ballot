import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useSelector } from 'react-redux';
import { Grid, Button, Typography } from '@mui/material';
import MenuIcon from '@mui/icons-material/Menu';
import { RootState } from 'common/store';
import { HeaderActions } from './components/HeaderActions';
import { MobileModal } from '../MobileModal/MobileModal';
import { Footer } from '../Footer/Footer';
import styles from './Header.module.scss';

export const Header = () => {
  const navigate = useNavigate();
  const event = useSelector((state: RootState) => state.user.event);
  const [isMobileMenuVisible, setIsMobileMenuVisible] = useState(false);

  const handleLogoClick = () => {
    navigate('/');
  };

  return (
    <>
      <Grid
        data-testid="header"
        container
        direction={{ xs: 'column', md: 'row' }}
        justifyContent={{ xs: 'center', md: 'space-between' }}
        className={styles.container}
        alignContent={{ xs: 'space-between', md: 'center' }}
        justifySelf={{
          xs: 'flex-start',
        }}
        height={{ xs: '69px', md: '69px' }}
      >
        <Grid
          item
          xs={12}
          md="auto"
          display="flex"
          className={styles.content}
          alignItems="center"
          marginBottom={{ xs: '0px', md: '0px' }}
          marginTop={{ xs: '0px', md: '0px' }}
        >
          <Typography
            data-testid="header-logo"
            lineHeight={{ sx: '23px', md: '29px' }}
            fontSize={{ sx: '20px', md: '24px' }}
            onClick={handleLogoClick}
            className={styles.logo}
          >
            CIP-1694 Ratification
          </Typography>
        </Grid>
        <Grid display={{ xs: 'none', md: 'flex' }}>
          <HeaderActions showNavigationItems={!event?.notStarted} />
        </Grid>
        <Grid
          display={{ xs: 'block', md: 'none' }}
          item
          md="auto"
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
            showNavigationItems={!event?.notStarted}
            onClick={() => setIsMobileMenuVisible(false)}
            isMobileMenu
          />
          <Footer isMobileMenu />
        </Grid>
      </MobileModal>
    </>
  );
};
