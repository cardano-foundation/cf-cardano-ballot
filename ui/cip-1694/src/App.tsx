import React, { useCallback, useEffect } from 'react';
import { Toaster } from 'react-hot-toast';
import { useDispatch } from 'react-redux';
import CssBaseline from '@mui/material/CssBaseline';
import { env } from 'env';
import * as referenceDataService from 'common/api/referenceDataService';
import { setEventData } from 'common/store/userSlice';
import { Layout } from 'components/common/Layout/Layout';
import Content from './components/common/Content/Content';
import { Footer } from './components/common/Footer/Footer';
import { Header } from './components/common/Header/Header';
import styles from './App.module.scss';

export const App = () => {
  const dispatch = useDispatch();
  const fetchEvent = useCallback(async () => {
    const event = await referenceDataService.getEvent(env.EVENT_ID);
    dispatch(setEventData({ event }));
  }, [dispatch]);

  useEffect(() => {
    fetchEvent();
  }, [fetchEvent]);

  return (
    <Layout>
      <Header />
      <Content />
      <Footer />
      <Toaster
        toastOptions={{
          className: styles.toast,
        }}
      />
      <CssBaseline />
    </Layout>
  );
};
