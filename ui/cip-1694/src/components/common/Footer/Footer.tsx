import React from 'react';
import Box from '@mui/material/Box';
import Typography from '@mui/material/Typography';
import { ReactComponent as DiscordIcon } from 'common/resources/images/discord-icon.svg';
import styles from './Footer.module.scss';
import TAndC from './resources/T&C.pdf';
import Privacy from './resources/Privacy.pdf';

const Copyright = () => (
  <Typography className={styles.copyright}>
    <span
      data-testid="copyright"
      color="inherit"
    >
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
    data-testid="footer"
    borderTop={{
      xs: '1px solid #bbb',
      md: 'none',
    }}
    marginTop={{ xs: '40px', md: '0px' }}
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
        <a
          href={TAndC}
          type="application/pdf"
          data-testid="t-and-c"
          className={styles.underline}
          target="_blank"
          rel="noreferrer"
        >
          Terms & Conditions
        </a>
      </span>
      <span className={styles.link}>
        <a
          href={Privacy}
          type="application/pdf"
          data-testid="privacy"
          className={styles.underline}
          target="_blank"
          rel="noreferrer"
        >
          Privacy
        </a>
      </span>
      <span
        data-testid="status"
        className={styles.link}
      >
        Version 1.01&nbsp;<span className={styles.underline}>(Status)</span>
      </span>
      <Box
        marginTop={{ xs: '15px', md: '0px' }}
        marginLeft={{ xs: '-5px', md: '0px' }}
      >
        <span
          data-testid="discord"
          className={styles.link}
        >
          <DiscordIcon />
        </span>
      </Box>
    </Box>
  </Box>
);
