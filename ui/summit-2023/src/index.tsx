import React from 'react';
import { createRoot } from 'react-dom/client';
import { Provider } from 'react-redux';
import CssBaseline from '@mui/material/CssBaseline';
import { ThemeProvider } from '@mui/material/styles';
import theme from './common/styles/theme';
import App from './App';
import reportWebVitals from './reportWebVitals';
import './index.scss';
import { setupStore } from './store/index';

const container = document.getElementById('root')!;
const root = createRoot(container);
const store = setupStore();

root.render(
  <Provider store={store}>
    <ThemeProvider theme={theme}>
      <CssBaseline />
      <App />
    </ThemeProvider>
  </Provider>
);

// If you want to start measuring performance in your app, pass a function
// to log results (for example: reportWebVitals(console.log))
// or send to an analytics endpoint. Learn more: https://bit.ly/CRA-vitals
// TODO: do wee need this in production build
reportWebVitals();
