import React, { useCallback, useEffect, useState } from 'react';
import { Footer } from './components/common/Footer/Footer';
import { BrowserRouter } from 'react-router-dom';
import './App.scss';
import { useDispatch, useSelector } from 'react-redux';
import {setEventData, setUserVotes, setWalletIsLoggedIn, setWalletIsVerified} from './store/userSlice';
import { Box, CircularProgress, Container, useMediaQuery, useTheme } from '@mui/material';
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
import {parseError} from 'common/constants/errors';
import {getUserVotes} from 'common/api/voteService';

function App() {
  const theme = useTheme();
  const isTablet = useMediaQuery(theme.breakpoints.down('lg'));
  const eventCache = useSelector((state: RootState) => state.user.event);
  const [storedValue, _] = useLocalStorage(CB_TERMS_AND_PRIVACY, false);
  const [openTermDialog, setOpenTermDialog] = useState(false);
  const { isConnected, stakeAddress } = useCardano({ limitNetwork: resolveCardanoNetwork(env.TARGET_NETWORK) });

  const dispatch = useDispatch();
  const fetchEvent = useCallback(async () => {
    try {
      const event = await getEvent(env.EVENT_ID);
      const staticCategories: CategoryContent[] = SUMMIT2023CONTENT.categories;

      const joinedCategories = event.categories
        .map((item1) => {
          const item2 = staticCategories.find((item) => item.id === item1.id);
          if (item2) {
            return { ...item1, ...item2 };
          }
          return null;
        })
        .filter((item) => item !== null);

      event.categories = joinedCategories;
      dispatch(setEventData({ event }));

      if (isConnected) {
        try  {
          const isVerified = await getIsVerified(env.EVENT_ID, stakeAddress);
          dispatch(setWalletIsVerified({ isVerified: isVerified.verified }));
        } catch (e) {
          eventBus.publish('showToast', parseError(e.message), 'error');
        }
      }

      const isLoggedIn = getUserInSession();

      if (isLoggedIn) {
        const isExpired = tokenIsExpired(isLoggedIn.expiresAt);
        if (!isExpired) {
          dispatch(setWalletIsLoggedIn({ isLoggedIn: isExpired }));
        } else {
          eventBus.publish('openLoginModal');
        }
      } else {
        eventBus.publish('openLoginModal');
      }
    } catch (error: any) {
      if (process.env.NODE_ENV === 'development') {
        console.log(`Failed to fetch event, ${error?.info || error?.message || error?.toString()}`);
      }
      eventBus.publish('showToast', parseError(error.message), 'error');
    }
  }, [dispatch, stakeAddress]);

  useEffect(() => {
    const session = getUserInSession();
    if (!tokenIsExpired(session?.expiresAt)) {
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
  }, []);

  useEffect(() => {
    fetchEvent();
  }, [fetchEvent]);

  useEffect(() => {
    setOpenTermDialog(!storedValue);
  }, []);

  return (
    <>
      <BrowserRouter>
        <img
          src={'/static/home-graphic-bg-top.svg'}
          alt="Home graphic background top left"
          className="home-graphic-bg-top"
        />
        <div
          className="App"
          style={{ padding: isTablet ? '0px 0px' : '10px 52px' }}
        >
          <Header />
          <div className="main-content">
            <Container
              maxWidth="xl"
              className="container"
            >
              <Box
                my={2}
                className="content"
              >
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
            </Container>
          </div>
          <img
            src={'/static/home-graphic-bg-bottom.svg'}
            alt="Home graphic background bottom right"
            className="home-graphic-bg-bottom"
          />
          <Footer />
          <TermsOptInModal
            open={openTermDialog}
            setOpen={(value) => setOpenTermDialog(value)}
          />
        </div>
      </BrowserRouter>
    </>
  );
}

export default App;
