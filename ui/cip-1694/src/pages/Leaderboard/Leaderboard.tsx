import React, { useCallback, useEffect, useMemo, useState } from 'react';
import { useSelector } from 'react-redux';
import cn from 'classnames';
import { capitalize } from 'lodash';
import toast from 'react-hot-toast';
import { PieChart } from 'react-minimal-pie-chart';
import BigNumber from 'bignumber.js';
import { Grid, Typography, IconButton } from '@mui/material';
import BlockIcon from '@mui/icons-material/Block';
import InfoOutlinedIcon from '@mui/icons-material/InfoOutlined';
import { ByProposalsInCategoryStats, Votes } from 'types/voting-app-types';
import { ChainTip, ProposalPresentation } from 'types/voting-ledger-follower-types';
import * as voteService from 'common/api/voteService';
import * as leaderboardService from 'common/api/leaderboardService';
import { RootState } from 'common/store';
import { Toast } from 'components/Toast/Toast';
import { Tooltip } from 'components/Tooltip/Tooltip';
import { getPercentage, proposalColorsMap, formatNumber, lovelacesToAdaString } from './utils';
import { StatsTile } from './components/StatsTile';
import styles from './Leaderboard.module.scss';
import { StatItem } from './types';

type Stats = { title: React.ReactNode; subTitle?: React.ReactNode; value: keyof Votes };
const statsConfig: Array<Stats> = [
  {
    title: (
      <>
        Total number of ballot submissions{' '}
        <Tooltip title="Total number of ballots cast">
          <IconButton sx={{ margin: '-8px' }}>
            <InfoOutlinedIcon style={{ color: '#39486CA6', fontSize: '19px' }} />
          </IconButton>
        </Tooltip>
      </>
    ),
    subTitle: 'Number of votes',
    value: 'votes',
  },
  {
    title: (
      <>
        Total ballot power{' '}
        <Tooltip title="Total power of wallets determined by the amount of ada staked at the time of the Snapshot (November 21st, before 21:44 UTC).">
          <IconButton sx={{ margin: '-8px' }}>
            <InfoOutlinedIcon style={{ color: '#39486CA6', fontSize: '19px' }} />
          </IconButton>
        </Tooltip>
      </>
    ),
    subTitle: 'Voting power',
    value: 'votingPower',
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
            votes: acc.votes + votes,
            votingPower: acc.votingPower.add(votingPower),
          };
        },
        {
          votes: 0,
          votingPower: new BigNumber(0),
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
          item
          container
          spacing={0}
          gap="25px"
          sx={{ flexWrap: { md: 'nowrap', xs: 'wrap' } }}
        >
          {statsConfig.map(({ title, subTitle, value }) => (
            <StatsTile
              key={value}
              title={title}
              dataTestId={`poll-stats-tile-${value}`}
              summary={
                <span style={{ color: '#061d3c' }}>
                  {!shouldShowPlaceholder
                    ? value === 'votes'
                      ? formatNumber(statsSum?.[value] || 0)
                      : lovelacesToAdaString(statsSum?.[value] || 0)
                    : placeholder}
                </span>
              }
            >
              <Grid
                container
                spacing={0}
                direction="column"
                gap="15px"
                marginTop="25px"
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
                    {subTitle}
                  </Typography>
                </Grid>
                {statsItems.map(({ label, name, id }) => (
                  <React.Fragment key={name}>
                    <div className={styles.divider} />
                    <Grid
                      container
                      justifyContent="space-between"
                      data-testid={`poll-stats-item-${value}`}
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
                          ? value === 'votes'
                            ? formatNumber(stats?.[id]?.[value] || 0)
                            : lovelacesToAdaString(stats?.[id]?.[value] || 0)
                          : placeholder}
                      </Typography>
                    </Grid>
                  </React.Fragment>
                ))}
              </Grid>
              <Grid
                container
                marginTop="25px"
                flexWrap="nowrap"
              >
                <Grid
                  container
                  item
                  flexDirection="column"
                  justifyContent="center"
                  gap="15px"
                >
                  {statsItems.map(({ label, name, id }) => (
                    <Grid
                      container
                      key={name}
                      gap="15px"
                      data-testid={`currently-voting-item-${value}`}
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
                              {getPercentage(stats?.[id]?.[value], statsSum?.[value]?.toString())}%
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
                  justifyContent="flex-end"
                >
                  <PieChart
                    style={{ height: '200px', width: '200px' }}
                    lineWidth={32}
                    data={stats ? getStatsItems(value) : [{ title: '', value: 1, color: '#BBBBBB' }]}
                  />
                </Grid>
              </Grid>
            </StatsTile>
          ))}
        </Grid>
      </Grid>
    </div>
  );
};
