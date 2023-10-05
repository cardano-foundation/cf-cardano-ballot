import React, { useCallback, useEffect, useMemo, useState } from 'react';
import { Typography, Grid, Box } from '@mui/material';
import styles from './Leaderboard.module.scss';
import cn from 'classnames';

import { PieChart } from 'react-minimal-pie-chart';
import { ByCategoryStats } from 'types/voting-app-types';
import { EventPresentation } from 'types/voting-ledger-follower-types';
import * as leaderboardService from '../../common/api/leaderboardService';
import { categoryColorsMap, getPercentage } from './utils';
import { StatItem } from './types';
import { useSelector } from 'react-redux';
import { RootState } from '../../store';
import { StatsTile } from './components/StatsTile';
import SUMMIT2023CONTENT from '../../common/resources/data/summit2023Content.json';
import { CategoryContent } from 'pages/Categories/Category.types';
import { eventBus } from '../../utils/EventBus';

const Leaderboard = () => {
  const event = useSelector((state: RootState) => state.user.event);
  const [stats, setStats] = useState<ByCategoryStats[]>();
  const summit2023Categories: CategoryContent[] = SUMMIT2023CONTENT.categories;
  const init = useCallback(async () => {
    try {
      await leaderboardService.getStats().then((response) => {
        setStats(response.categories);
      });
    } catch (error) {
      const message = `Failed to fecth stats: ${error?.message || error?.toString()}`;
      if (process.env.NODE_ENV === 'development') {
        console.log(message);
      }
      eventBus.publish('showToast', 'Failed to fecth stats', 'error');
    }
  }, []);

  useEffect(() => {
    init();
  }, [init]);

  const statsItems: StatItem<EventPresentation['categories']>[] =
    event?.categories?.map(({ id }, index) => ({
      id,
      label: id === summit2023Categories[index].id && summit2023Categories[index].presentationName,
    })) || [];

  const placeholder = '--';
  const statsSum = useMemo(() => stats && Object.values(stats)?.reduce((acc, { votes }) => (acc += votes), 0), [stats]);

  const chartData = statsItems.map(({ label, id }) => ({
    title: label,
    value: stats?.find((category) => category.id === id)?.votes,
    color: categoryColorsMap[id],
  }));

  return (
    <div
      data-testid="leaderboard-page"
      className={styles.leaderboard}
    >
      <div
        style={{
          display: 'flex',
          justifyContent: 'space-between',
          alignItems: 'center',
          marginBottom: 48,
        }}
      >
        <Typography
          variant="h2"
          fontSize={{
            xs: '28px',
            md: '32px',
            lg: '48px',
          }}
          lineHeight={{
            xs: '28px',
            md: '32px',
          }}
          sx={{
            color: '#24262E',
            fontStyle: 'normal',
            fontWeight: '600',
          }}
        >
          Leaderboard
        </Typography>
      </div>
      <Grid
        container
        spacing={0}
        gridRow={{ md: 6, xs: 12 }}
        gap={{ md: '46px', xs: '25px' }}
        sx={{ flexWrap: { md: 'nowrap', xs: 'wrap' } }}
      >
        <StatsTile
          title="Total Votes"
          dataTestId="total-votes-tile"
          summary={<span style={{ color: '#061d3c' }}>{statsSum || placeholder}</span>}
        >
          <Grid
            container
            spacing={0}
            direction="column"
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
                Category
              </Typography>
              <Typography
                variant="h5"
                className={styles.optionTitle}
              >
                Number of votes
              </Typography>
            </Grid>
            {statsItems.map(({ label, id }) => (
              <React.Fragment key={id}>
                <div className={styles.divider} />
                <Grid
                  container
                  justifyContent="space-between"
                  data-testid="total-stats-item"
                  sx={{ my: '15px' }}
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
                    {stats?.find((category) => category.id === id).votes || placeholder}
                  </Typography>
                </Grid>
              </React.Fragment>
            ))}
          </Grid>
        </StatsTile>
        <StatsTile
          title="Votes Per Category"
          dataTestId="votes-per-category"
        >
          <Grid
            container
            direction={{ xs: 'column' }}
            gridRow={{ md: 7, xs: 12 }}
            sx={{ flexWrap: { md: 'nowrap', xs: 'wrap' }, marginTop: { md: '8px', xs: '25px' } }}
            gap={{ xs: '15px', md: 'none' }}
          >
            <Grid
              item
              container
              justifyContent={{ xs: 'center' }}
            >
              <PieChart
                style={{ height: '350px', width: '350px', margin: 2 }}
                lineWidth={45}
                data={statsSum > 0 ? chartData : [{ title: '', value: 1, color: '#BBBBBB' }]}
              />
            </Grid>

            <Box sx={{ flexGrow: 1 }}>
              <Grid
                container
                spacing={{ xs: 2 }}
                columns={{ xs: 4, sm: 8, md: 12 }}
              >
                {statsItems.map(({ label, id }) => (
                  <Grid
                    item
                    xs={2}
                    sm={4}
                    md={4}
                    key={id}
                  >
                    <Grid
                      container
                      spacing={1}
                      key={id}
                    >
                      <Grid
                        item
                        xs={1}
                      >
                        <div
                          className={styles.proposalRect}
                          data-proposal={id}
                        />
                      </Grid>
                      <Grid
                        item
                        xs={11}
                      >
                        <Grid
                          container
                          direction="row"
                          sx={{ pl: 1 }}
                        >
                          <Grid
                            item
                            xs={12}
                          >
                            <Typography
                              variant="h4"
                              className={cn(styles.optionTitle, styles.statTitle)}
                            >
                              {label}
                            </Typography>
                          </Grid>
                          <Grid
                            item
                            xs={12}
                            sx={{ fontWeight: 600 }}
                          >
                            {stats && (
                              <>
                                <span style={{ color: '#39486C' }}>
                                  {statsSum > 0
                                    ? getPercentage(
                                        stats?.find((category) => category.id === id)?.votes,
                                        statsSum
                                      ).toFixed(2)
                                    : '0'}{' '}
                                  %
                                </span>
                                <span style={{ color: '#BBBBBB' }}>{' - '}</span>
                                <span style={{ color: '#BBBBBB' }}>
                                  {stats?.find((category) => category.id === id)?.votes}
                                </span>
                              </>
                            )}
                          </Grid>
                        </Grid>
                      </Grid>
                    </Grid>
                  </Grid>
                ))}
              </Grid>
            </Box>
          </Grid>
        </StatsTile>
      </Grid>
    </div>
  );
};

export { Leaderboard };
