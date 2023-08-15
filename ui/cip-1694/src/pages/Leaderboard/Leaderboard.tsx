import React, { useCallback, useEffect, useMemo, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useSelector } from 'react-redux';
import cn from 'classnames';
import toast from 'react-hot-toast';
import { PieChart } from 'react-minimal-pie-chart';
import { Grid, Typography } from '@mui/material';
import ErrorOutlineIcon from '@mui/icons-material/ErrorOutline';
import { useCardano } from '@cardano-foundation/cardano-connect-with-wallet';
import { ByCategory, ProposalReference } from 'types/backend-services-types';
import { ROUTES } from 'common/routes';
import { RootState } from 'common/store';
import * as leaderboardService from 'common/api/leaderboardService';
import { Toast } from 'components/common/Toast/Toast';
import { proposalColorsMap } from './utils';
import { StatsTile } from './components/StatsTile';
import { env } from '../../env';
import styles from './Leaderboard.module.scss';
import { StatItem } from './types';

const getPercentage = (value: number, total: number) => (value * 100) / total;

export const Leaderboard = () => {
  const navigate = useNavigate();
  const { isConnected } = useCardano();
  const event = useSelector((state: RootState) => state.user.event);
  const eventHasntStarted = !event?.active && !event?.finished;
  const [stats, setStats] = useState<ByCategory['proposals']>();

  useEffect(() => {
    if (!isConnected || eventHasntStarted || !event?.finished) navigate(ROUTES.INTRO);
  }, [event?.finished, eventHasntStarted, isConnected, navigate]);

  const init = useCallback(async () => {
    try {
      setStats((await leaderboardService.getStats())?.proposals);
    } catch (error) {
      const message = `Failed to fecth stats: ${error?.message || error?.toString()}`;
      console.log(message);
      toast(
        <Toast
          message={message}
          icon={<ErrorOutlineIcon style={{ color: '#cc0e00' }} />}
        />
      );
    }
  }, []);

  useEffect(() => {
    if (isConnected) {
      init();
    }
  }, [init, isConnected]);

  const statsItems: StatItem<ProposalReference['name']>[] = event?.categories
    ?.find(({ id }) => id === env.CATEGORY_ID)
    ?.proposals?.map(({ name, presentationName: label }) => ({
      name,
      label,
    }));

  const statsSum = useMemo(() => stats && Object.values(stats)?.reduce((acc, { votes }) => (acc += votes), 0), [stats]);

  return (
    <div className={styles.leaderboard}>
      <Grid
        paddingTop={{ xs: '20px', md: '30px' }}
        container
        direction="column"
        justifyContent="left"
        alignItems="left"
        spacing={0}
      >
        <Grid item>
          <Typography
            variant="h5"
            className={styles.title}
            fontSize={{
              xs: '28px',
              md: '56px',
            }}
            lineHeight={{
              xs: '33px',
              md: '65px',
            }}
            marginBottom={{ md: '40px', xs: '25px' }}
          >
            Leaderboard
          </Typography>
        </Grid>
        <Grid
          container
          spacing={0}
          gridRow={{ md: 6, xs: 12 }}
          gap={{ md: '46px', xs: '25px' }}
          sx={{ flexWrap: { md: 'nowrap', xs: 'wrap' } }}
        >
          <StatsTile
            title="Poll stats"
            summary={<span style={{ color: '#061d3c' }}>{statsSum}</span>}
          >
            <Grid
              container
              spacing={0}
              direction="column"
              gap="15px"
              sx={{ marginTop: '25px' }}
            >
              <Grid
                container
                justifyContent="space-between"
              >
                <Typography
                  variant="h5"
                  className={styles.optionTitle}
                >
                  Answer
                </Typography>
                <Typography
                  variant="h5"
                  className={styles.optionTitle}
                >
                  Number of votes
                </Typography>
              </Grid>
              {statsItems.map(({ label, name }) => (
                <React.Fragment key={name}>
                  <div className={styles.divider} />
                  <Grid
                    container
                    justifyContent="space-between"
                  >
                    <Typography
                      variant="h5"
                      className={cn(styles.optionTitle, styles.statTitle)}
                    >
                      {label}
                    </Typography>
                    <Typography
                      variant="h5"
                      className={cn(styles.optionTitle, styles.statTitle)}
                    >
                      {stats?.[name]?.votes}
                    </Typography>
                  </Grid>
                </React.Fragment>
              ))}
            </Grid>
          </StatsTile>
          <StatsTile
            title="Current voting stats"
            summary={<span style={{ color: '#061d3c' }}>{statsSum}</span>}
          >
            <Grid
              container
              direction={{ md: 'row', xs: 'column-reverse' }}
              gridRow={{ md: 6, xs: 12 }}
              sx={{ flexWrap: { md: 'nowrap', xs: 'wrap' }, marginTop: { md: '8px', xs: '25px' } }}
              gap={{ xs: '25px', md: 'none' }}
            >
              <Grid
                container
                item
                justifyContent="center"
                direction="column"
                gap="15px"
              >
                {statsItems.map(({ label, name }) => (
                  <Grid
                    container
                    key={name}
                    gap="15px"
                  >
                    <div
                      className={styles.proposalRect}
                      data-proposal={name}
                    />
                    <Typography
                      variant="h5"
                      className={cn(styles.optionTitle, styles.statTitle)}
                    >
                      {label}
                      <span style={{ color: '#BBBBBB' }}>&nbsp;-</span>
                      <span style={{ color: '#39486C' }}>&nbsp;{getPercentage(stats?.[name]?.votes, statsSum)}%</span>
                    </Typography>
                  </Grid>
                ))}
              </Grid>
              <Grid
                item
                container
                justifyContent={{ md: 'space-between', xs: 'center' }}
              >
                <PieChart
                  style={{ height: '200px', width: '200px' }}
                  lineWidth={32}
                  data={statsItems.map(({ label, name }) => ({
                    title: label,
                    value: 10,
                    color: proposalColorsMap[name],
                  }))}
                />
              </Grid>
            </Grid>
          </StatsTile>
        </Grid>
      </Grid>
    </div>
  );
};
