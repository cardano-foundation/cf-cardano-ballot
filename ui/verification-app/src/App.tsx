import React from 'react';
import { PageRoutes } from 'common/routes';
import { Toaster } from 'react-hot-toast';
import styles from './App.module.scss';

export const App = () => (
  <>
    <PageRoutes />;
    <Toaster toastOptions={{ className: styles.toast }} />
  </>
);
