import React, { useCallback, useEffect, useState } from 'react';
import { useCardano } from '@cardano-foundation/cardano-connect-with-wallet';
import { Box, CircularProgress, Container, Grid, useMediaQuery, useTheme } from '@mui/material';
import { useDispatch, useSelector } from 'react-redux';
import { BrowserRouter } from 'react-router-dom';

import { Footer } from './components/common/Footer/Footer';
import { setEventData, setUserVotes, setWalletIsLoggedIn, setWalletIsVerified, setWinners } from './store/userSlice';
import Header from './components/common/Header/Header';
import { PageRouter } from './routes';
import { env } from './common/constants/env';
import { RootState } from './store';
import { useLocalStorage } from './common/hooks/useLocalStorage';
import { getIsVerified } from 'common/api/verificationService';
import { getEvent } from 'common/api/referenceDataService';
import { getUserInSession, tokenIsExpired } from './utils/session';
import { CB_TERMS_AND_PRIVACY } from './common/constants/local';
import { TermsOptInModal } from 'components/LegalOptInModal';
import { eventBus } from './utils/EventBus';
import { CategoryContent } from './pages/Categories/Category.types';
import SUMMIT2023CONTENT from 'common/resources/data/summit2023Content.json';
import { resolveCardanoNetwork } from './utils/utils';
import { parseError } from 'common/constants/errors';
import { getUserVotes } from 'common/api/voteService';
import { getWinners } from 'common/api/leaderboardService';
import './App.scss';

function App() {
  const dispatch = useDispatch();
  const theme = useTheme();
  const isBigScreen = useMediaQuery(theme.breakpoints.up('lg'));

  const eventCache = useSelector((state: RootState) => state.user.event);
  const walletIsVerified = useSelector((state: RootState) => state.user.walletIsVerified);

  const [termsAndConditionsChecked] = useLocalStorage(CB_TERMS_AND_PRIVACY, false);
  const [openTermDialog, setOpenTermDialog] = useState(false);

  const session = getUserInSession();
  const isExpired = tokenIsExpired(session?.expiresAt);

  const { isConnected, stakeAddress } = useCardano({ limitNetwork: resolveCardanoNetwork(env.TARGET_NETWORK) });

  const fetchEvent = useCallback(async () => {
    try {
      const event = await getEvent(env.EVENT_ID);
      const staticCategories: CategoryContent[] = SUMMIT2023CONTENT.categories;

      const joinedCategories = event.categories
        .map((category) => {
          const joinedCategory = staticCategories.find((staticCategory) => staticCategory.id === category.id);
          if (joinedCategory) {
            return { ...category, ...joinedCategory };
          }
          return null;
        })
        .filter((staticCategory) => staticCategory !== null);

      event.categories = joinedCategories;
      dispatch(setEventData({ event }));

      if (isConnected && !eventCache.finished) {
        try {
          const isVerified = await getIsVerified(env.EVENT_ID, stakeAddress);
          dispatch(setWalletIsVerified({ isVerified: isVerified.verified }));
        } catch (e) {
          if (process.env.NODE_ENV === 'development') {
            console.log(e.message);
          }
        }
      }

      if ('finished' in event && event.finished) {
        try {
          const winners = await getWinners();
          dispatch(setWinners({ winners }));
        } catch (e) {
          if (process.env.NODE_ENV === 'development') {
            console.log(e.message);
          }
        }
      }

      if (session) {
        dispatch(setWalletIsLoggedIn({ isLoggedIn: !isExpired }));
        if (!isExpired) {
          getUserVotes(session?.accessToken)
            .then((response) => {
              if (response) {
                dispatch(setUserVotes({ userVotes: response }));
              }
            })
            .catch((e) => {
              eventBus.publish('showToast', parseError(e.message), 'error');
            });
        }
      }
    } catch (error: any) {
      if (process.env.NODE_ENV === 'development') {
        console.log(`Failed to fetch event, ${error?.info || error?.message || error?.toString()}`);
      }

      if (error.message !== 'EVENT_NOT_FOUND') eventBus.publish('showToast', parseError(error.message), 'error');
    }
  }, [dispatch, stakeAddress]);

  useEffect(() => {
    fetchEvent();
  }, [fetchEvent, stakeAddress]);

  useEffect(() => {
    const queryParams = new URLSearchParams(location.search);
    const action = queryParams.get('action');
    const secret = queryParams.get('secret');

    const isVerifiedEventNotEnded = walletIsVerified && !eventCache.finished;
    const notVerifiedEventEnded = !walletIsVerified && eventCache.finished;
    const sessionExpired = !session || isExpired;
    const notDiscordVerification = !(action === 'verification' && secret.includes('|'));

    const showLoginModal =
      termsAndConditionsChecked &&
      isConnected &&
      notDiscordVerification &&
      ((isVerifiedEventNotEnded && sessionExpired) || notVerifiedEventEnded);

    if (showLoginModal) {
      eventBus.publish('openLoginModal', 'If you already voted, please login to see your votes.');
    }
  }, [isConnected]);

  useEffect(() => {
    setOpenTermDialog(!termsAndConditionsChecked);
  }, []);

  return (
    <Container maxWidth={isBigScreen ? 'lg' : 'xl'}>
      <BrowserRouter>
        <img
          src={'/static/home-graphic-bg-top.svg'}
          alt="Home graphic background top left"
          className="home-graphic-bg-top"
        />
        <Grid
          container
          spacing={1}
          direction="column"
        >
          <Grid
            item
            xs
          >
            <Header />
          </Grid>
          <Grid
            item
            xs={6}
            sx={{ maxWidth: '100% !important' }}
          >
            <Box className="content">
              {eventCache !== undefined && eventCache?.id.length ? (
                <PageRouter />
              ) : (
                <Box
                  sx={{
                    display: 'flex',
                    height: '60vh',
                    alignItems: 'center',
                    justifyContent: 'center',
                  }}
                >
                  <CircularProgress
                    className="app-spinner"
                    style={{
                      color: '#03021f',
                      strokeWidth: '10',
                    }}
                  />
                </Box>
              )}
            </Box>
          </Grid>
          <Grid
            item
            xs
          >
            <Footer />
          </Grid>
        </Grid>
        <TermsOptInModal
          open={openTermDialog}
          setOpen={(value) => setOpenTermDialog(value)}
        />
      </BrowserRouter>
    </Container>
  );
}

export default App;
