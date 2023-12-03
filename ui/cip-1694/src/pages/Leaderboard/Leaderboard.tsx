import React, { useCallback, useEffect, useMemo, useState } from 'react';
import { useSelector } from 'react-redux';
import cn from 'classnames';
import { capitalize } from 'lodash';
import toast from 'react-hot-toast';
import { PieChart } from 'react-minimal-pie-chart';
import { Grid, Typography } from '@mui/material';
import BlockIcon from '@mui/icons-material/Block';
import { useCardano } from '@cardano-foundation/cardano-connect-with-wallet';
import { ByProposalsInCategoryStats } from 'types/voting-app-types';
import { ProposalPresentation } from 'types/voting-ledger-follower-types';
import { RootState } from 'common/store';
import * as leaderboardService from 'common/api/leaderboardService';
import { Toast } from 'components/Toast/Toast';
import { getPercentage, proposalColorsMap } from './utils';
import { StatsTile } from './components/StatsTile';
import styles from './Leaderboard.module.scss';
import { StatItem } from './types';

export const Leaderboard = () => {
  const { isConnected } = useCardano();
  const event = useSelector((state: RootState) => state.user.event);
  const [stats, setStats] = useState<ByProposalsInCategoryStats['proposals']>();

  const init = useCallback(async () => {
    if (!event?.categories?.[0]?.id) return;
    try {
      setStats((await leaderboardService.getStats(event?.categories?.[0]?.id))?.proposals);
    } catch (error) {
      const message = `Failed to fetch stats: ${error?.message || error?.toString()}`;
      toast(
        <Toast
          error
          message={message}
          icon={<BlockIcon style={{ fontSize: '19px', color: '#F5F9FF' }} />}
        />
      );
    }
  }, [event?.categories]);

  const canViewResults = useMemo(() => isConnected && event?.finished === true, [event?.finished, isConnected]);

  useEffect(() => {
    if (canViewResults) {
      init();
    }
  }, [init, canViewResults]);

  const statsItems: StatItem<ProposalPresentation['name']>[] =
    event?.categories?.[0]?.proposals?.map(({ name, id }) => ({
      id,
      name,
      label: capitalize(name.toLowerCase()),
    })) || [];

  const placeholder = '--';
  const statsSum = useMemo(() => stats && Object.values(stats)?.reduce((acc, { votes }) => (acc += votes), 0), [stats]);

  const chartData = statsItems.map(({ label, name, id }) => ({
    title: label,
    value: stats?.[id]?.votes,
    color: proposalColorsMap[name],
  }));

  return (
    <div
      data-testid="leaderboard-page"
      className={styles.leaderboard}
    >
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
            data-testid="leaderboard-title"
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
            title="Ballot stats"
            dataTestId="poll-stats-tile"
            summary={<span style={{ color: '#061d3c' }}>{statsSum || placeholder}</span>}
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
              {statsItems.map(({ label, name, id }) => (
                <React.Fragment key={name}>
                  <div className={styles.divider} />
                  <Grid
                    container
                    justifyContent="space-between"
                    data-testid="poll-stats-item"
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
                      {stats?.[id]?.votes || placeholder}
                    </Typography>
                  </Grid>
                </React.Fragment>
              ))}
            </Grid>
          </StatsTile>
          <StatsTile
            title="Current ballot stats"
            summary={<span style={{ color: '#061d3c' }}>{statsSum || placeholder}</span>}
            dataTestId="currently-voting-tile"
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
                {statsItems.map(({ label, name, id }) => (
                  <Grid
                    container
                    key={name}
                    gap="15px"
                    data-testid="currently-voting-item"
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
                      {stats && (
                        <>
                          <span style={{ color: '#BBBBBB' }}>{' - '}</span>

                          <span style={{ color: '#39486C' }}>
                            {getPercentage(stats?.[id]?.votes, statsSum).toFixed(2)}%
                          </span>
                        </>
                      )}
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
                  data={stats ? chartData : [{ title: '', value: 1, color: '#BBBBBB' }]}
                />
              </Grid>
            </Grid>
          </StatsTile>
        </Grid>
      </Grid>
    </div>
  );
};
