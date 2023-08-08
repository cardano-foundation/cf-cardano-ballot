import React from 'react';
import { Grid, Typography } from '@mui/material';
import styles from './StatsTile.module.scss';

type StatsTilePorps = {
  title: string | React.ReactElement;
  summary: string | React.ReactElement;
  children: React.ReactNode;
};

export const StatsTile = ({ title, summary, children }: StatsTilePorps) => {
  return (
    <Grid
      xs={12}
      md={6}
      item
      className={styles.optionCard}
      padding={{ md: '30px', xs: '20px' }}
    >
      <Grid
        container
        spacing={0}
        direction="column"
        gap="10px"
      >
        <Typography
          variant="h5"
          className={styles.optionTitle}
        >
          {title}
        </Typography>
        <Typography
          variant="h5"
          sx={{
            fontSize: { md: '40px', xs: '28px' },
            lineHeight: { md: '47px', xs: '32px' },
          }}
          className={styles.optionSummary}
        >
          {summary}
        </Typography>
      </Grid>
      {children}
    </Grid>
  );
};
