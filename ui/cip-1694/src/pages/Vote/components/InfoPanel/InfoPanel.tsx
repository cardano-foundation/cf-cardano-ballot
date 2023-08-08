import React from 'react';
import Grid from '@mui/material/Grid';
import cn from 'classnames';
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

export const InfoPanel = ({ icon, title, cta, description, type }: InfoPanelProps) => (
  <Grid
    gap="16px"
    className={cn(styles.infoPanel, { [styles[type?.toLowerCase()]]: type })}
  >
    {icon || IconsMap[type]}
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
        <span className={styles.title}>{title}</span>
        {cta}
      </Grid>
      <Grid
        item
        justifyContent="space-between"
      >
        <span className={styles.description}>{description}</span>
      </Grid>
    </Grid>
  </Grid>
);
