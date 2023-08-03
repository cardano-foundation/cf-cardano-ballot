import React from 'react';
import Box from '@mui/material/Box';
import Typography from '@mui/material/Typography';
import { ReactComponent as DiscordIcon } from 'common/resources/images/discord-icon.svg';
import styles from './Footer.module.scss';

const Copyright = () => (
  <Typography className={styles.copyright}>
    <span color="inherit">
      © {new Date().getFullYear()}{' '}
      <Box
        component={'span'}
        display={{ xs: 'none', md: 'inline' }}
      >
        CIP-1694
      </Box>{' '}
      Ratification. All rights reserved.
    </span>
  </Typography>
);

export const Footer = ({ isMobileMenu = false }) => (
  <Box
    borderTop={{
      xs: '1px solid #bbb',
      md: 'none',
    }}
    marginTop={{ xs: '60px', md: '0px' }}
    paddingTop={{ xs: '40px', md: '0px' }}
    alignItems={{ xs: 'flex-start', md: 'center' }}
    flexDirection={{ xs: 'column', md: 'row' }}
    height={{ xs: 'auto', md: '85px' }}
    className={styles.footer}
  >
    {!isMobileMenu && (
      <Box marginBottom={{ xs: '30px', md: '0px' }}>
        <Copyright />
      </Box>
    )}

    <Box
      flexDirection={{ xs: 'column', md: 'row' }}
      gap={{ xs: '15px', md: '10px' }}
      sx={{
        display: 'flex',
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
      <Box
        marginTop={{ xs: '15px', md: '0px' }}
        marginLeft={{ xs: '-5px', md: '0px' }}
      >
        <span className={styles.link}>
          <DiscordIcon />
        </span>
      </Box>
    </Box>
  </Box>
);
