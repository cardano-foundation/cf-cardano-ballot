import React from 'react';
import { Toaster } from 'react-hot-toast';
import CssBaseline from '@mui/material/CssBaseline';
import { Layout } from 'components/common/Layout/Layout';
import Content from './components/common/Content/Content';
import { Footer } from './components/common/Footer/Footer';
import { Header } from './components/common/Header/Header';
import styles from './App.module.scss';

export const App = () => (
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
