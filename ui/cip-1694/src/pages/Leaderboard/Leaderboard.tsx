import React, { useCallback, useEffect, useMemo, useState } from 'react';
import { useSelector } from 'react-redux';
import cn from 'classnames';
import { capitalize } from 'lodash';
import toast from 'react-hot-toast';
import { PieChart } from 'react-minimal-pie-chart';
import { Grid, Typography } from '@mui/material';
import BlockIcon from '@mui/icons-material/Block';
import { ByProposalsInCategoryStats, Votes } from 'types/voting-app-types';
import { ChainTip, ProposalPresentation } from 'types/voting-ledger-follower-types';
import * as voteService from 'common/api/voteService';
import * as leaderboardService from 'common/api/leaderboardService';
import { RootState } from 'common/store';
import { Toast } from 'components/Toast/Toast';
import { getPercentage, proposalColorsMap, formatNumber } from './utils';
import { StatsTile } from './components/StatsTile';
import styles from './Leaderboard.module.scss';
import { StatItem } from './types';

type Stats = { title: string; subTitle?: string; value: keyof Votes };
const statsConfig: Array<{ pollStats: Stats; votingStats: Stats }> = [
  {
    pollStats: { title: 'Ballot stats', subTitle: 'Number of votes', value: 'votes' },
    votingStats: { title: 'Current ballot stats', value: 'votes' },
  },
  {
    pollStats: { title: 'Ballot stats', subTitle: 'Voting power', value: 'votingPower' },
    votingStats: { title: 'Voting power', value: 'votingPower' },
  },
];

export const Leaderboard = () => {
  const event = useSelector((state: RootState) => state.user.event);
  const [stats, setStats] = useState<ByProposalsInCategoryStats['proposals']>();

  const fetchChainTip = useCallback(async () => {
    let chainTip: ChainTip = null;
    try {
      chainTip = await voteService.getChainTip();
    } catch (error) {
      toast(
        <Toast
          message="Failed to fetch chain tip"
          error
          icon={<BlockIcon style={{ fontSize: '19px', color: '#F5F9FF' }} />}
        />
      );
    }
    return chainTip;
  }, []);

  const init = useCallback(async () => {
    const chainTip = await fetchChainTip();
    if (!event || !chainTip || event.proposalsRevealEpoch > chainTip.epochNo) return;

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
  }, [event, fetchChainTip]);

  const canViewResults = useMemo(() => event?.finished === true, [event?.finished]);

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

  const shouldShowPlaceholder = !event || !stats;
  const placeholder = '--';
  const statsSum = useMemo(
    () =>
      stats &&
      Object.values(stats)?.reduce(
        (acc, { votes, votingPower }) => {
          return {
            votes: (acc.votes += votes),
            votingPower: (acc.votingPower += BigInt(votingPower)),
          };
        },
        {
          votes: 0,
          votingPower: BigInt(0),
        }
      ),
    [stats]
  );

  const getStatsItems = (key: keyof Votes) =>
    statsItems.map(({ label, name, id }) => ({
      title: label,
      value: getPercentage(stats?.[id]?.[key], statsSum?.[key]?.toString()),
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
          gridRow={{ xs: 12 }}
          gap={{ xs: '25px' }}
        >
          {statsConfig.map(({ pollStats, votingStats }) => (
            <Grid
              key={pollStats.value}
              container
              spacing={0}
              gridRow={{ md: 6, xs: 12 }}
              gap={{ md: '46px', xs: '25px' }}
              sx={{ flexWrap: { md: 'nowrap', xs: 'wrap' } }}
            >
              <StatsTile
                title={pollStats.title}
                dataTestId={`poll-stats-tile-${votingStats.value}`}
                summary={
                  <span style={{ color: '#061d3c' }}>
                    {!shouldShowPlaceholder ? formatNumber(statsSum?.[pollStats.value] || 0)?.toString() : placeholder}
                  </span>
                }
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
                      {pollStats.subTitle}
                    </Typography>
                  </Grid>
                  {statsItems.map(({ label, name, id }) => (
                    <React.Fragment key={name}>
                      <div className={styles.divider} />
                      <Grid
                        container
                        justifyContent="space-between"
                        data-testid={`poll-stats-item-${votingStats.value}`}
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
                          {!shouldShowPlaceholder
                            ? formatNumber(BigInt(stats?.[id]?.[pollStats.value] || 0))
                            : placeholder}
                        </Typography>
                      </Grid>
                    </React.Fragment>
                  ))}
                </Grid>
              </StatsTile>
              <StatsTile
                title={votingStats.title}
                summary={
                  <span style={{ color: '#061d3c' }}>
                    {!shouldShowPlaceholder
                      ? formatNumber(BigInt(statsSum?.[votingStats.value] || 0))?.toString()
                      : placeholder}
                  </span>
                }
                dataTestId={`currently-voting-tile-${votingStats.value}`}
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
                        data-testid={`currently-voting-item-${votingStats.value}`}
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
                                {getPercentage(
                                  stats?.[id]?.[votingStats.value],
                                  statsSum?.[votingStats.value]?.toString()
                                )}
                                %
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
                      data={stats ? getStatsItems(votingStats.value) : [{ title: '', value: 1, color: '#BBBBBB' }]}
                    />
                  </Grid>
                </Grid>
              </StatsTile>
            </Grid>
          ))}
        </Grid>
      </Grid>
    </div>
  );
};
