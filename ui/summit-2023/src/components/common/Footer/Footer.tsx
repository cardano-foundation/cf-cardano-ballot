import React from 'react';
import { Typography, Grid } from '@mui/material';
import styles from './Footer.module.scss';
import discordLogo from '../../../common/resources/images/discord-icon.svg';
import { env } from 'common/constants/env';
import { NavLink } from 'react-router-dom';
import { i18n } from 'i18n';
import { openNewTab } from '../../../utils/utils';

const Footer: React.FC = () => {
  return (
    <Grid
      container
      direction={{ sm: 'column', md: 'row' }}
      justifyContent="center"
      alignItems="center"
      className={styles.footer}
    >
      <Grid
        item
        xs={12}
        sm={6}
      >
        <Typography variant="body2">
          © {new Date().getFullYear()}{' '}
          <NavLink
            to="https://summit.cardano.org/"
            target="_blank"
            rel="noopener"
          >
            Cardano Summit
          </NavLink>
          . <span color="inherit">All rights reserved.</span>
        </Typography>
      </Grid>
      <Grid
        item
        xs={12}
        sm={6}
      >
        <Grid
          container
          direction={{ sm: 'column', md: 'row' }}
          sx={{ textAlignLast: { xs: 'center', sm: 'right' } }}
        >
          <Grid
            item
            xs={12}
            sm={3}
          >
            <NavLink to="/termsandconditions">
              <Typography
                variant="body2"
                justifyContent="center"
                className={styles.link}
              >
                {i18n.t('footer.menu.termsAndConditions')}
              </Typography>
            </NavLink>
          </Grid>

          <Grid
            item
            xs={12}
            sm={3}
          >
            <NavLink to="/privacypolicy">
              <Typography
                variant="body2"
                justifyContent="center"
                className={styles.link}
              >
                {i18n.t('footer.menu.privacyPolicy')}
              </Typography>
            </NavLink>
          </Grid>

          <Grid
            item
            xs={12}
            sm={4}
          >
            <Typography
              variant="body2"
              justifyContent="center"
            >
              Version {env.APP_VERSION} (Status)
            </Typography>
          </Grid>

          <Grid
            item
            xs={12}
            sm={2}
          >
            <Typography variant="body2">
              <img
                onClick={() => openNewTab(env.DISCORD_CHANNEL_URL)}
                src={discordLogo}
                alt="Discord"
                style={{ height: '25px', cursor: 'pointer' }}
              />
            </Typography>
          </Grid>
        </Grid>
      </Grid>
    </Grid>
  );
};

export { Footer };
