import React from 'react';
import { createRoot } from 'react-dom/client';
import { BrowserRouter as Router } from 'react-router-dom';
import { Provider } from 'react-redux';
import { ThemeProvider } from '@mui/material/styles';
import reportWebVitals from './reportWebVitals';
import { App } from './App';
import theme from './common/styles/theme';
import { setupStore } from './common/store/index';

const container = document.getElementById('root')!;
const root = createRoot(container);
const store = setupStore();

root.render(
  <Provider store={store}>
    <ThemeProvider theme={theme}>
      <Router>
        <App />
      </Router>
    </ThemeProvider>
  </Provider>
);

// If you want to start measuring performance in your app, pass a function
// to log results (for example: reportWebVitals(console.log))
// or send to an analytics endpoint. Learn more: https://bit.ly/CRA-vitals
// TODO: do wee need this in production build
reportWebVitals();
