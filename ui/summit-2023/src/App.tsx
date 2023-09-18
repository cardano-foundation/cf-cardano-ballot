import React, { useCallback, useEffect } from 'react';
import { Footer } from './components/common/Footer/Footer';
import { BrowserRouter } from 'react-router-dom';
import './App.scss';
import { useDispatch, useSelector } from 'react-redux';
import { setEventData, setWalletIsLoggedIn, setWalletIsVerified } from './store/userSlice';
import BackgroundPolygon1 from './common/resources/images/polygon1.svg';
import { Box, CircularProgress, Container, useMediaQuery, useTheme } from '@mui/material';
import Header from './components/common/Header/Header';
import { PageRouter } from './routes';
import { env } from './common/constants/env';
import { RootState } from './store';
import { useCardano } from '@cardano-foundation/cardano-connect-with-wallet';
import { getIsVerified } from 'common/api/verificationService';
import { getEvent } from 'common/api/referenceDataService';
import { getUserInSession, tokenIsExpired } from './utils/session';
import { NetworkType } from '@cardano-foundation/cardano-connect-with-wallet-core';
import { eventBus } from './utils/EventBus';

function App() {
  const theme = useTheme();
  const isTablet = useMediaQuery(theme.breakpoints.down('lg'));
  const eventCache = useSelector((state: RootState) => state.user.event);
  const { isConnected, stakeAddress } = useCardano({ limitNetwork: 'testnet' as NetworkType });

  const dispatch = useDispatch();
  const fetchEvent = useCallback(async () => {
    try {
      const event = await getEvent(env.EVENT_ID);
      dispatch(setEventData({ event }));

      if (isConnected) {
        const isVerified = await getIsVerified(env.EVENT_ID, stakeAddress);
        dispatch(setWalletIsVerified({ isVerified: isVerified.verified }));
      }

      const isLoggedIn = getUserInSession();

      if (isLoggedIn) {
        const isExpired = tokenIsExpired(isLoggedIn.expiresAt);
        if (!isExpired) dispatch(setWalletIsLoggedIn({ isLoggedIn: isExpired }));
      }
    } catch (error: any) {
      if (process.env.NODE_ENV === 'development') {
        console.log(`Failed to fetch event, ${error?.info || error?.message || error?.toString()}`);
      }
      eventBus.publish('showToast', 'Failed to update event', true);
    }
  }, [dispatch, stakeAddress]);

  useEffect(() => {
    fetchEvent();
  }, [fetchEvent]);
  return (
    <>
      <BrowserRouter>
        <img
          src={BackgroundPolygon1}
          alt="Background Shape"
          className="background-shape-1"
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
          <Footer />
        </div>
      </BrowserRouter>
    </>
  );
}

export default App;
