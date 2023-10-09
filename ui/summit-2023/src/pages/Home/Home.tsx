import React from 'react';
import { NavLink } from 'react-router-dom';
import { Typography, Grid, useTheme, useMediaQuery, Box } from '@mui/material';
import Chip from '@mui/material/Chip';
import EventIcon from '@mui/icons-material/Event';
import CARDANOSUMMIT2023LOGO from '../../common/resources/images/cardanosummit2023.svg';
import { Hexagon } from '../../components/common/Hexagon';
import './Home.scss';
import { i18n } from '../../i18n';
import { CustomButton } from '../../components/common/Button/CustomButton';
import { useSelector } from 'react-redux';
import { RootState } from '../../store';
import { formatUTCDate } from 'utils/dateUtils';

const Home: React.FC = () => {
  const theme = useTheme();
  const isMobile = useMediaQuery(theme.breakpoints.down('sm'));
  const eventCache = useSelector((state: RootState) => state.user.event);

  return (
    <Grid
      container
      spacing={1}
      sx={{
        height: { xs: '60%' },
        margin: { xs: '0%', sm: '2.5%' },
      }}
    >
      <Grid
        item
        xs={12}
        sm={12}
        md={6}
        sx={{
          flex: '1',
          padding: '20px',
          order: '1',
          display: 'flex',
          justifyContent: 'center',
          alignItems: 'center',
        }}
      >
        <div className="left-title-container">
          <Typography
            className="title"
            variant="h2"
            sx={{
              textAlign: { xs: 'center', sm: 'center', md: 'left' },
              fontSize: { xs: '32px', sm: '48px', md: '56px' },
            }}
          >
            {i18n.t('landing.title')}
          </Typography>
          {isMobile ? (
            <div className="event-time">
              <Box
                className="custom-chip-mobile"
                sx={{ justifyContent: 'center' }}
              >
                <EventIcon sx={{ mt: 1 }} />
                Voting closes {formatUTCDate(eventCache?.eventEndDate?.toString())}.
              </Box>
            </div>
          ) : (
            <Box sx={{ textAlign: { xs: 'center', sm: 'center', md: 'left' } }}>
              <Chip
                sx={{
                  height: '46px',
                  borderRadius: '8px',
                  my: '20px',
                  px: '10px',
                }}
                icon={<EventIcon />}
                label={`Voting closes ${formatUTCDate(eventCache?.eventEndDate?.toString())}.`}
                color="primary"
              />
            </Box>
          )}

          <Typography
            variant="body1"
            sx={{ textAlign: { xs: 'center', sm: 'center', md: 'left' } }}
          >
            {i18n.t('landing.description')}
          </Typography>

          <Grid
            container
            spacing={1}
            sx={{ justifyContent: { xs: 'center', sm: 'center', md: 'left' } }}
          >
            <Grid
              item
              xs={12}
              sm={5}
            >
              <NavLink
                to="/categories"
                style={{ textDecoration: 'none' }}
              >
                <CustomButton
                  styles={{
                    background: '#ACFCC5',
                    color: '#03021F',
                    marginTop: '20px',
                    textDecoration: 'none !important',
                  }}
                  fullWidth
                  label={
                    eventCache?.finished ? i18n.t('landing.votingEndedButton') : i18n.t('landing.getStartedButton')
                  }
                />
              </NavLink>
            </Grid>

            {!eventCache?.finished && (
              <Grid
                item
                xs={12}
                sm={5}
                sx={{ mt: { xs: '0px', sm: '20px' } }}
              >
                <NavLink
                  to="/user-guide"
                  style={{ textDecoration: 'none' }}
                >
                  <CustomButton
                    styles={{
                      background: 'transparent !important',
                      color: '#03021F',
                      border: '1px solid #daeefb',
                    }}
                    fullWidth
                    label={i18n.t('landing.howToVoteButton')}
                  />
                </NavLink>
              </Grid>
            )}
          </Grid>
        </div>
      </Grid>

      <Grid
        item
        xs={12}
        sm={12}
        md={6}
        justifyContent="center"
        alignItems="center"
        sx={{
          display: 'flex',
          order: '1',
        }}
      >
        <div className="hero-banner">
          <Hexagon>
            <>
              <img
                src={CARDANOSUMMIT2023LOGO}
                alt="CARDANO SUMMIT 2023"
                className="cardano-summit-logo"
              />
            </>
          </Hexagon>
        </div>
      </Grid>
    </Grid>
  );
};

export { Home };
