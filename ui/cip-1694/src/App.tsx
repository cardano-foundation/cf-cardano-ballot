import React, { useCallback, useEffect } from 'react';
import toast, { Toaster } from 'react-hot-toast';
import { useDispatch } from 'react-redux';
import CssBaseline from '@mui/material/CssBaseline';
import ErrorOutlineIcon from '@mui/icons-material/ErrorOutline';
import { env } from 'env';
import * as referenceDataService from 'common/api/referenceDataService';
import { setEventData } from 'common/store/userSlice';
import { Layout } from 'components/common/Layout/Layout';
import { Toast } from 'components/common/Toast/Toast';
import Content from './components/common/Content/Content';
import { Footer } from './components/common/Footer/Footer';
import { Header } from './components/common/Header/Header';
import styles from './App.module.scss';

export const App = () => {
  const dispatch = useDispatch();
  const fetchEvent = useCallback(async () => {
    try {
      const event = await referenceDataService.getEvent(env.EVENT_ID);
      dispatch(setEventData({ event }));
    } catch (error) {
      const message = `Failed to fetch event, ${error?.info || error?.message || error?.toString()}`;
      toast(
        <Toast
          message={message}
          icon={<ErrorOutlineIcon style={{ color: '#cc0e00' }} />}
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
      <Toaster
        toastOptions={{
          className: styles.toast,
        }}
      />
      <CssBaseline />
    </Layout>
  );
};
