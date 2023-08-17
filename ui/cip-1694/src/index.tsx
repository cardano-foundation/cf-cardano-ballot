import React from 'react';
import { createRoot } from 'react-dom/client';
import { BrowserRouter as Router } from 'react-router-dom';
import { Provider } from 'react-redux';
import { ThemeProvider } from '@mui/material/styles';
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
