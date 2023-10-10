import React from 'react';
import { Typography, Grid, Box } from '@mui/material';
import CARDANOSUMMIT2023LOGO from '../../common/resources/images/cardanosummit2023.svg';
import { Hexagon } from '../../components/common/Hexagon';
import './Home.scss';
import { i18n } from '../../i18n';
import { NavLink } from 'react-router-dom';
import { CustomButton } from '../../components/common/Button/CustomButton';
import { useSelector } from 'react-redux';
import { RootState } from '../../store';
import { formatUTCDate } from 'utils/dateUtils';
import Chip from '@mui/material/Chip';
import EventIcon from '@mui/icons-material/Event';
import { Trans } from 'react-i18next';

const Home: React.FC = () => {
  const eventCache = useSelector((state: RootState) => state.user.event);
  const hasEventFinished = eventCache?.finished;

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
            { hasEventFinished
                  ? i18n.t('landing.eventFinishedTitle')
                  : i18n.t('landing.title')
            }
          </Typography>
          <Box sx={{ textAlign: { xs: 'center', sm: 'center', md: 'left' } }}>
            <Chip
              sx={{
                height: '46px',
                borderRadius: '8px',
                my: '20px',
                px: '10px',
              }}
              icon={<EventIcon />}
              label={
                hasEventFinished
                  ? 'Voting is now closed.'
                  : `Voting closes 11 October 2023 23:59 UTC.`
              }
              color="primary"
            />
          </Box>
          <Typography
            variant="body1"
            sx={{ textAlign: { xs: 'center', sm: 'center', md: 'left' } }}
          >
            <Trans i18nKey={ hasEventFinished ? 'landing.eventFinishedDescription' : 'landing.description'} components={{ bold: <strong /> }}  ></Trans>
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
                to={hasEventFinished ? '/leaderboard' : '/categories'}
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
                    hasEventFinished ? i18n.t('landing.votingLeaderboardButton') : i18n.t('landing.getStartedButton')
                  }
                />
              </NavLink>
            </Grid>

            {!hasEventFinished && (
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
