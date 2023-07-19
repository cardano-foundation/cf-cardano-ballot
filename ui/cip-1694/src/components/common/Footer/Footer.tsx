import React from 'react';
import Box from '@mui/material/Box';
import Typography from '@mui/material/Typography';
import { ReactComponent as Discord } from 'common/resources/images/discord-icon.svg';
import styles from './Footer.module.scss';

const Copyright = () => (
  <Typography
    variant="body2"
    color="text.secondary"
    align="center"
    sx={{
      color: '#39486C',
      fontSize: '16px',
      fontStyle: 'normal',
      fontWeight: '400',
      lineHeight: 'normal',
    }}
  >
    <span color="inherit">© {new Date().getFullYear()} CIP-1694 Ratification. All rights reserved.</span>
  </Typography>
);

export const Footer = () => (
  <Box
    sx={{
      display: 'flex',
      flexDirection: 'row',
      alignItems: 'center',
      justifyContent: 'space-between',
      height: '85px',
    }}
  >
    <Copyright />
    <Box
      sx={{
        display: 'flex',
        gap: '10px',
      }}
    >
      <span className={styles.link}>
        <span className={styles.underline}>Terms & Conditions</span>
      </span>
      <span className={styles.link}>
        <span className={styles.underline}>Privacy</span>
      </span>
      <span className={styles.link}>
        Version 1.01&nbsp;<span className={styles.underline}>(Status)</span>
      </span>
      <span className={styles.link}>
        <Discord />
      </span>
    </Box>
  </Box>
);
