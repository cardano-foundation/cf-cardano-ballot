import React, { useCallback, useEffect, useState } from 'react';
import { Footer } from './components/common/Footer/Footer';
import { BrowserRouter } from 'react-router-dom';
import './App.scss';
import { useDispatch, useSelector } from 'react-redux';
import { setEventData, setUserVotes, setWalletIsLoggedIn, setWalletIsVerified, setWinners } from './store/userSlice';
import { Box, CircularProgress, Container, Grid, useMediaQuery, useTheme } from '@mui/material';
import Header from './components/common/Header/Header';
import { PageRouter } from './routes';
import { env } from './common/constants/env';
import { RootState } from './store';
import { useLocalStorage } from './common/hooks/useLocalStorage';
import { useCardano } from '@cardano-foundation/cardano-connect-with-wallet';
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

function App() {
  const theme = useTheme();
  const eventCache = useSelector((state: RootState) => state.user.event);
  const [termsAndConditionsChecked] = useLocalStorage(CB_TERMS_AND_PRIVACY, false);
  const [openTermDialog, setOpenTermDialog] = useState(false);
  const { isConnected, stakeAddress } = useCardano({ limitNetwork: resolveCardanoNetwork(env.TARGET_NETWORK) });
  const session = getUserInSession();
  const isExpired = tokenIsExpired(session?.expiresAt);
  const isBigScreen = useMediaQuery(theme.breakpoints.up('lg'));

  const dispatch = useDispatch();
  const fetchEvent = useCallback(async () => {
    try {
      const event = await getEvent(env.EVENT_ID);
      /*if ('finished' in event) {
        event.finished = true;
      }*/
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
      eventBus.publish('showToast', parseError(error.message), 'error');
    }
  }, [dispatch, stakeAddress]);

  useEffect(() => {
    fetchEvent();
  }, [fetchEvent, stakeAddress]);

  useEffect(() => {
    const queryParams = new URLSearchParams(location.search);
    const action = queryParams.get('action');
    const secret = queryParams.get('secret');

    if (isConnected && (!session || isExpired) && !(action === 'verification' && secret.includes('|'))) {
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
          >
            <Box className="content">
              {eventCache !== undefined ? (
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
