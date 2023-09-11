import React from 'react';
import { Typography, Grid, Box } from '@mui/material';
import './Footer.scss';
import discordLogo from '../../../common/resources/images/discord-icon.svg';
import { env } from 'common/constants/env';

const Footer: React.FC = () => {
  return (
    <Box
      mt={5}
      textAlign="center"
      style={{ background: 'transparent', boxShadow: 'none', padding: '10px', bottom: '0 !important' }}
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
              <Typography
                variant="body2"
                align="center"
              >
                Terms & Conditions
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
                Privacy
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
                  src={discordLogo}
                  alt="Discord"
                  style={{ height: '25px' }}
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
