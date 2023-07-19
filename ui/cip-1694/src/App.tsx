import React from 'react';
import { BrowserRouter as Router } from 'react-router-dom';
import { Toaster } from 'react-hot-toast';
import CssBaseline from '@mui/material/CssBaseline';
import { ThemeProvider } from '@mui/material/styles';
import { Layout } from 'components/common/Layout/Layout';
import Content from './components/common/Content/Content';
import { Footer } from './components/common/Footer/Footer';
import { Header } from './components/common/Header/Header';
import theme from './common/styles/theme';

export const App = () => (
  <ThemeProvider theme={theme}>
    <Router>
      <Layout>
        <Header />
        <Content />
        <Footer />
        <Toaster
          toastOptions={{
            className: '',
            style: {
              borderRadius: '10px',
              background: '#030321',
              color: '#fff',
            },
          }}
        />
        <CssBaseline />
      </Layout>
    </Router>
  </ThemeProvider>
);
