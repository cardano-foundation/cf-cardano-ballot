import React, { useCallback, useEffect, useMemo, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { capitalize } from 'lodash';
import cn from 'classnames';
import { PieChart } from 'react-minimal-pie-chart';
import { Grid, Typography } from '@mui/material';
import { useCardano } from '@cardano-foundation/cardano-connect-with-wallet';
import { ByCategory } from 'types/backend-services-types';
import { ROUTES } from 'common/routes';
import * as leaderboardService from 'common/api/leaderboardService';
import { proposalColorsMap, proposalOptions } from './utils';
import { StatsTile } from './components/StatsTile';
import styles from './Leaderboard.module.scss';

const getPercentage = (value: number, total: number) => (value * 100) / total;

export const Leaderboard = () => {
  const navigate = useNavigate();
  const { isConnected } = useCardano();
  const [stats, setStats] = useState<ByCategory['proposals']>();

  useEffect(() => {
    if (!isConnected) navigate(ROUTES.INTRO);
  }, [isConnected, navigate]);

  const init = useCallback(async () => {
    try {
      setStats((await leaderboardService.getStats())?.proposals);
    } catch (error) {
      console.log('Failed to fecth stats', error?.message);
    }
  }, []);

  useEffect(() => {
    if (isConnected) {
      init();
    }
  }, [init, isConnected]);

  const statsSum = useMemo(() => stats && Object.values(stats)?.reduce((acc, { votes }) => (acc += votes), 0), [stats]);

  return (
    <div className={styles.leaderboard}>
      <Grid
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
          >
            Leaderboard
          </Typography>
        </Grid>
        <Grid
          container
          spacing={0}
          gridRow={{ sm: 6, xs: 12 }}
          gap="46px"
          wrap="nowrap"
        >
          <StatsTile
            title="Current voting stats"
            summary={<span>{statsSum}</span>}
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
              {proposalOptions.map((proposal) => (
                <React.Fragment key={proposal}>
                  <div className={styles.divider} />
                  <Grid
                    container
                    justifyContent="space-between"
                  >
                    <Typography
                      variant="h5"
                      className={cn(styles.optionTitle, styles.statTitle)}
                    >
                      {capitalize(proposal)}
                    </Typography>
                    <Typography
                      variant="h5"
                      className={cn(styles.optionTitle, styles.statTitle)}
                    >
                      {stats?.[proposal.toUpperCase()]?.votes}
                    </Typography>
                  </Grid>
                </React.Fragment>
              ))}
            </Grid>
          </StatsTile>
          <StatsTile
            title="Current voting stats"
            summary={<span>{statsSum}</span>}
          >
            <Grid
              container
              direction="row"
              gridRow={{ sm: 6, xs: 12 }}
              wrap="nowrap"
              sx={{ marginTop: '8px' }}
            >
              <Grid
                container
                item
                justifyContent="center"
                direction="column"
                gap="15px"
              >
                {proposalOptions.map((proposal) => (
                  <Grid
                    container
                    key={proposal}
                    gap="15px"
                  >
                    <div
                      className={styles.proposalRect}
                      data-proposal={proposal}
                    />
                    <Typography
                      variant="h5"
                      className={cn(styles.optionTitle, styles.statTitle)}
                    >
                      {capitalize(proposal)}
                      <span style={{ color: '#BBBBBB' }}>&nbsp;-</span>
                      <span style={{ color: '#39486C' }}>
                        &nbsp;{getPercentage(stats?.[proposal.toUpperCase()]?.votes, statsSum)}%
                      </span>
                    </Typography>
                  </Grid>
                ))}
              </Grid>
              <Grid
                item
                container
                justifyContent="space-between"
              >
                <PieChart
                  style={{ height: '200px', width: '200px' }}
                  lineWidth={32}
                  data={proposalOptions.map((proposal) => ({
                    title: capitalize(proposal),
                    value: 10,
                    color: proposalColorsMap[proposal],
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
