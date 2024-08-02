import React, { useCallback, useEffect } from 'react';
import toast, { Toaster } from 'react-hot-toast';
import { useDispatch } from 'react-redux';
import CssBaseline from '@mui/material/CssBaseline';
import BlockIcon from '@mui/icons-material/Block';
import * as referenceDataService from 'common/api/referenceDataService';
import { setEventData } from 'common/store/userSlice';
import { Layout } from 'components/Layout/Layout';
import { Toast } from 'components/Toast/Toast';
import { Content } from './components/Content/Content';
import { Footer } from './components/Footer/Footer';
import { Header } from './components/Header/Header';
import styles from './App.module.scss';
import { env } from './env';

export const App = () => {
  const dispatch = useDispatch();
  const fetchEvent = useCallback(async () => {
    try {
      const event = await referenceDataService.getEvent(env.EVENT_ID);
      dispatch(setEventData({ event }));
    } catch (error) {
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
    <Layout>
      <Header />
      <Content />
      <Footer />
      <Toaster toastOptions={{ className: styles.toast }} />
      <CssBaseline />
    </Layout>
  );
};
