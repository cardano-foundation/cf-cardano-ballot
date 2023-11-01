import React from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { Link } from 'react-router-dom';
import CssBaseline from '@mui/material/CssBaseline';
import { Grid, Container, Typography, Button, Box } from '@mui/material';
import { useCardano } from '@cardano-foundation/cardano-connect-with-wallet';
import { RootState } from 'common/store';
import { setIsConnectWalletModalVisible } from 'common/store/userSlice';
import { ROUTES } from 'common/routes';
import { EventTime } from 'components/EventTime/EventTime';
import styles from './Introduction.module.scss';

export const IntroductionPage = () => {
  const { isConnected } = useCardano();
  const event = useSelector((state: RootState) => state.user.event);
  const dispatch = useDispatch();

  return (
    <Box
      data-testid="introduction-page"
      margin={{
        xs: '0px',
        md: '43px 0px',
      }}
      className={styles.slides}
    >
      <div className={styles.swiperContainer}>
        <div className={styles.sliderWrapper}>
          <CssBaseline />
          <Container disableGutters>
            <Grid
              marginTop={{ xs: '5px', md: '0px' }}
              container
              direction={{ xs: 'column-reverse', md: 'row' }}
              justifyContent={{ xs: 'center', md: 'flex-start' }}
              alignItems={{ xs: 'flex-start', md: 'center' }}
              columnSpacing={{ md: '46px' }}
              gap={{ xs: '25px', md: '0px' }}
            >
              <Grid
                container
                item
                xs={6}
                display="flex"
                direction="column"
                alignItems="flex-start"
              >
                <Typography
                  variant="h2"
                  className={styles.title}
                  data-testid="event-title"
                  fontSize={{
                    xs: '28px',
                    md: '56px',
                  }}
                  lineHeight={{
                    xs: '33px',
                    md: '65px',
                  }}
                >
                  Cardano Ballot on CIP-1694
                </Typography>
                <Typography
                  component={'span'}
                  sx={{
                    mb: '24px',
                    width: '100%',
                  }}
                  fontSize={{
                    xs: '16px',
                    md: '18px',
                  }}
                >
                  <EventTime
                    eventHasntStarted={event?.notStarted}
                    eventHasFinished={event?.finished}
                    endTime={event?.eventEndDate?.toString()}
                    startTime={event?.eventStartDate?.toString()}
                  />
                </Typography>
                <Typography
                  variant="body1"
                  className={styles.description}
                  data-testid="event-description"
                  marginBottom={{
                    xs: '25px',
                    md: '40px',
                  }}
                >
                  Cardano has reached an incredible milestone. After six years of initial development and feature
                  cultivation, the Cardano blockchain has reached the age of Voltaire. Guided by a principles-first
                  approach and led by the community, this new age of Cardano advances inclusive accountability for all
                  participants in the ecosystem. Now is the time for the community to help guide our journey toward a
                  shared future by participating in the Cardano Ballot on the deployment of on-chain governance, as
                  described in CIP-1694.
                </Typography>
                {event?.notStarted ? (
                  <Button
                    size="large"
                    component={isConnected ? Link : undefined}
                    onClick={
                      !isConnected ? () => dispatch(setIsConnectWalletModalVisible({ isVisible: true })) : undefined
                    }
                    variant="contained"
                    className={styles.button}
                    data-testid="event-cta"
                    to={isConnected ? { pathname: ROUTES['VOTE'] } : undefined}
                  >
                    {isConnected ? 'Preview the question' : 'Get started'}
                  </Button>
                ) : (
                  <Button
                    size="large"
                    component={Link}
                    variant="contained"
                    className={styles.button}
                    data-testid="event-cta"
                    to={{ pathname: ROUTES[event?.finished ? 'LEADERBOARD' : 'VOTE'] }}
                  >
                    {event?.finished ? 'See the results' : 'Get started'}
                  </Button>
                )}
              </Grid>
              <Grid
                item
                xs={6}
                height="auto"
                width={{ xs: '100%', md: '550px' }}
              >
                <img
                  className={styles.heroStyleImg}
                  data-testid="event-image"
                  src="/static/cip-1694.jpg"
                  alt="cardano-summit-2022"
                />
              </Grid>
            </Grid>
          </Container>
        </div>
      </div>
    </Box>
  );
};
