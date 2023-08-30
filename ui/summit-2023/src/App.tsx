import React, { useCallback, useEffect } from 'react';
import { Footer } from './components/common/Footer/Footer';
import { BrowserRouter } from 'react-router-dom';
import './App.scss';
import { useDispatch } from 'react-redux';
import toast, { Toaster } from 'react-hot-toast';
import BlockIcon from '@mui/icons-material/Block';
import { Toast } from './components/common/Toast/Toast';
import { setEventData } from './common/store/userSlice';
import BackgroundPolygon1 from './common/resources/images/polygon1.svg';
import { Box, Container, useMediaQuery, useTheme } from '@mui/material';
import * as referenceDataService from './common/api/referenceDataService';
import { Box, Container } from '@mui/material';
import Header from './components/common/Header/Header';
import { PageRouter } from './routes';
import { env } from './common/constants/env';

function App() {
  const theme = useTheme();
  const isMobile = useMediaQuery(theme.breakpoints.down('sm'));

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
          message="Failed to fetch event"
          error
          icon={<BlockIcon style={{ fontSize: '19px', color: '#F5F9FF' }} />}
        />
      );
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
                <PageRouter />
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
