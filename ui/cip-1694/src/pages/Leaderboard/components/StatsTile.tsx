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
      sm={6}
      item
      className={styles.optionCard}
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
          className={styles.optionSummary}
        >
          {summary}
        </Typography>
      </Grid>
      {children}
    </Grid>
  );
};
