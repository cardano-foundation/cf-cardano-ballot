import React, { useCallback, useEffect } from 'react';
import { Footer } from './components/common/Footer/Footer';
import { BrowserRouter } from 'react-router-dom';
import './App.scss';
import { useDispatch, useSelector } from 'react-redux';
import toast, { Toaster } from 'react-hot-toast';
import BlockIcon from '@mui/icons-material/Block';
import { Toast } from './components/common/Toast/Toast';
import { setEventData } from './store/userSlice';
import BackgroundPolygon1 from './common/resources/images/polygon1.svg';
import { Box, CircularProgress, Container, useMediaQuery, useTheme } from '@mui/material';
import * as referenceDataService from './common/api/referenceDataService';
import Header from './components/common/Header/Header';
import { PageRouter } from './routes';
import { env } from 'common/constants/env';
import eventData from './common/resources/data/event.json';
import { EventPresentation } from './types/voting-ledger-follower-types';
import { RootState } from './store';

function App() {
  const theme = useTheme();
  const isMobile = useMediaQuery(theme.breakpoints.down('sm'));
  const eventCache = useSelector((state: RootState) => state.user.event);

  const dispatch = useDispatch();
  const fetchEvent = useCallback(async () => {
    try {
      const event = await referenceDataService.getEvent(env.EVENT_ID);
      dispatch(setEventData({ event }));
    } catch (error: any) {
      if (process.env.NODE_ENV === 'development') {
        console.log(`Failed to fetch event, ${error?.info || error?.message || error?.toString()}`);
      }
      toast(
        <Toast
          message="Failed to update event"
          error
          icon={<BlockIcon style={{ fontSize: '19px', color: '#F5F9FF' }} />}
        />
      );
      const e = eventData as EventPresentation;
      dispatch(setEventData({ event: e }));
    }
  }, [dispatch]);

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
          style={{ padding: isMobile ? '0px 0px' : '10px 52px' }}
        >
          <Header />
          <div className="main-content">
            <Container
              maxWidth="xl"
              className="big-container"
            >
              <Box my={2}>
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
          <Toaster toastOptions={{ className: 'toast' }} />
        </div>
      </BrowserRouter>
    </>
  );
}

export default App;
