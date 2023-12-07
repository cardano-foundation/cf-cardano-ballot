import React from 'react';
import cn from 'classnames';
import { Grid, Tooltip as MuiTooltip, Typography } from '@mui/material';
import styles from './Tooltip.module.scss';

export const Tooltip = ({
  title,
  children,
  fullWidth = false,
}: {
  title: React.ReactNode;
  children?: React.ReactElement;
  fullWidth?: boolean;
}) => (
  <MuiTooltip
    classes={{ tooltip: cn(styles.tooltip, { [styles.tooltipFullWidth]: fullWidth }) }}
    title={
      <Grid
        container
        direction="column"
        alignItems="left"
        gap={'8px'}
      >
        <Typography
          className={styles.tooltipDescription}
          variant="h4"
        >
          {title}
        </Typography>
      </Grid>
    }
  >
    {children}
  </MuiTooltip>
);
