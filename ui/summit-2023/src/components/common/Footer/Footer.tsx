import React from 'react';
import { Typography, Grid, Box } from '@mui/material';
import styles from './Footer.module.scss';
import discordLogo from '../../../common/resources/images/discord-icon.svg';
import { env } from 'common/constants/env';
import { NavLink } from 'react-router-dom';
import { i18n } from 'i18n';
import {openNewTab} from '../../../utils/utils';

const Footer: React.FC = () => {
  return (
    <Box
      mt={1}
      textAlign="center"
      className={styles.footer}
    >
      <Grid container>
        <Grid
          item
          xs={12}
          sm={4}
        >
          <Typography variant="body2">
            © {new Date().getFullYear()} Cardano Summit.
            <span color="inherit"> All rights reserved.</span>
          </Typography>
        </Grid>
        <Grid
          item
          xs={12}
          sm={8}
        >
          <Grid container>
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
              sm={3}
            >
              <Typography
                variant="body2"
                align="center"
              >
                Version {env.APP_VERSION} (Status)
              </Typography>
            </Grid>

            <Grid
              item
              xs={12}
              sm={3}
            >
              <Typography
                variant="body2"
                align="center"
              >
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
    </Box>
  );
};

export { Footer };
