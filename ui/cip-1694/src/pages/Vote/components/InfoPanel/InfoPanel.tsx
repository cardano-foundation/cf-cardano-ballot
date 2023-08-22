import React from 'react';
import Grid from '@mui/material/Grid';
import cn from 'classnames';
import { Box } from '@mui/material';
import NotificationsNoneIcon from '@mui/icons-material/NotificationsNone';
import WarningAmberIcon from '@mui/icons-material/WarningAmber';
import GppBadOutlinedIcon from '@mui/icons-material/GppBadOutlined';
import GppGoodOutlinedIcon from '@mui/icons-material/GppGoodOutlined';
import styles from './InfoPanel.module.scss';

export enum InfoPanelTypes {
  DEFAULT = 'DEFAULT',
  SUCCESS = 'SUCCESS',
  WARNING = 'WARNING',
  ERROR = 'ERROR',
}

type InfoPanelProps = {
  icon?: React.ReactNode;
  title: React.ReactNode;
  cta: React.ReactNode;
  description?: React.ReactNode;
  type: InfoPanelTypes;
  isSm?: boolean;
};

const IconsMap: Record<InfoPanelTypes, React.ReactElement> = {
  [InfoPanelTypes.DEFAULT]: (
    <span className={styles.iconWrapper}>
      <NotificationsNoneIcon className={styles.icon} />
    </span>
  ),
  [InfoPanelTypes.SUCCESS]: (
    <span className={cn(styles.iconWrapper, styles.success)}>
      <GppGoodOutlinedIcon className={styles.icon} />
    </span>
  ),
  [InfoPanelTypes.WARNING]: (
    <span className={cn(styles.iconWrapper, styles.warning)}>
      <WarningAmberIcon className={styles.icon} />
    </span>
  ),
  [InfoPanelTypes.ERROR]: (
    <span className={cn(styles.iconWrapper, styles.error)}>
      <GppBadOutlinedIcon className={styles.icon} />
    </span>
  ),
};

export const InfoPanel = ({ icon, title, cta, description, type, isSm }: InfoPanelProps) => (
  <Grid
    gap="16px"
    className={cn(styles.infoPanel, { [styles[type?.toLowerCase()]]: type })}
    container
    direction={{ xs: 'column', sm: 'row' }}
    flexWrap="nowrap"
    data-testid="receipt-info"
  >
    {!isSm && (
      <Grid
        gap="0"
        item
        display={{ xs: 'flex', sm: 'none' }}
        justifyContent="space-between"
      >
        <Grid item>{icon || IconsMap[type]}</Grid>
        <Grid item>{cta}</Grid>
      </Grid>
    )}

    <Grid
      display={{ xs: 'none', sm: 'flex' }}
      item
    >
      {icon || IconsMap[type]}
    </Grid>
    <Grid
      container
      direction="column"
      gap={description ? '5px' : '0px'}
    >
      <Grid
        item
        container
        justifyContent="space-between"
      >
        <span
          data-testid="receipt-info-title"
          className={styles.title}
        >
          {title}
        </span>
        {isSm && <Box>{cta}</Box>}
      </Grid>
      <Grid
        item
        justifyContent="space-between"
      >
        <span
          data-testid="receipt-info-description"
          className={styles.description}
        >
          {description}
        </span>
      </Grid>
    </Grid>
  </Grid>
);
