import React from 'react';
import { Grid } from '@mui/material';
import styles from './Layout.module.scss';

type LayoutProps = {
  children: React.ReactNode;
};

export const Layout = ({ children }: LayoutProps) => (
  <Grid
    container
    direction={{ xs: 'column', md: 'row' }}
    justifyContent={'center'}
    className={styles.container}
    data-testid="layout"
  >
    <Grid
      flex={{ xs: '1', md: 'none' }}
      className={styles.content}
      maxWidth="100%"
      width={{ xs: '100%', md: '100%', lg: '1440px' }}
    >
      {children}
    </Grid>
  </Grid>
);
