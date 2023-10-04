import React from 'react';
import { Typography, Grid, IconButton, Tooltip } from '@mui/material';
import styles from './Footer.module.scss';
import discordLogo from '../../../common/resources/images/discord-icon.svg';
import ContactSupportOutlinedIcon from '@mui/icons-material/ContactSupportOutlined';
import { env } from 'common/constants/env';
import { NavLink } from 'react-router-dom';
import { i18n } from 'i18n';
import { openNewTab } from '../../../utils/utils';

const Footer: React.FC = () => {
  return (
    <Grid
      container
      spacing={1}
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
          spacing={1}
          direction={{ sm: 'column', md: 'row' }}
          sx={{ textAlignLast: { xs: 'center', sm: 'center', md: 'right' } }}
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
              Version {env.APP_VERSION}
              <NavLink
                to={'https://status2023.voting.summit.cardano.org/'}
                target="_blank"
              >
                <span
                  className={styles.link}
                >
                  Status
                </span>
              </NavLink>
            </Typography>
          </Grid>

          <Grid
            item
            xs={12}
            sm={1}
          >
            <Tooltip
              title="Get support"
              placement="top"
              >
              <IconButton
                onClick={() => openNewTab(env.DISCORD_SUPPORT_CHANNEL_URL)}
                sx={{ m: -1 }}
                >
                <ContactSupportOutlinedIcon/>
              </IconButton>
            </Tooltip>
          </Grid>

          <Grid
            item
            xs={12}
            sm={1}
          >
            <Tooltip
              title="Join our Discord"
              placement="top"
              >
              <Typography
                variant="body2"
                >
                <img
                  onClick={() => openNewTab(env.DISCORD_CHANNEL_URL)}
                  src={discordLogo}
                  alt="Discord"
                  style={{ height: '25px', cursor: 'pointer' }}
                />
              </Typography>
            </Tooltip>
          </Grid>
        </Grid>
      </Grid>
    </Grid>
  );
};

export { Footer };
