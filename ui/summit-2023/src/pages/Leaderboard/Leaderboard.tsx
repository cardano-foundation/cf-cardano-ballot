import React, { useCallback, useEffect, useMemo, useState } from 'react';
import { Typography, Grid, Box, styled } from '@mui/material';
import styles from './Leaderboard.module.scss';
import cn from 'classnames';
import { makeStyles } from 'tss-react/mui';
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
import Masonry from 'react-masonry-css';
import { AwardsTile } from './components/AwardsTile';
import Tab from '@mui/material/Tab';
import TabContext from '@mui/lab/TabContext';
import TabList from '@mui/lab/TabList';
import TabPanel from '@mui/lab/TabPanel';
import WinnersIcon from '@mui/icons-material/EmojiEvents';
import VotesIcon from '@mui/icons-material/DonutLarge';
import BarChartIcon from '@mui/icons-material/BarChart';
import { env } from '../../common/constants/env';
import { HydraTile } from './components/HydraTile';
import Tooltip, { tooltipClasses, TooltipProps } from '@mui/material/Tooltip';

const WinnersDisabledText =
  'Winners will be revealed following the Cardano Summit 2023 Gala Awards Dinner on 4 November.';
const HydraTallyDisabledText =
  'Hydra Tally results will be available on-chain following the Cardano Summit 2023 Awards Gala Dinner on 4 November.';

const useStyles = makeStyles()(() => ({
  customTab: {
    '& .MuiTab-root': {
      color: '#03021f',
      textTransform: 'capitalize',
      height: 3,
      '&.Mui-selected': {
        color: '#106593',
        '&.svg': {
          fill: '#106593 !important',
        },
      },
      '&:hover': {
        backgroundColor: '#DAEEFB',
      },
      '&:active': {
        backgroundColor: '#DAEEFB',
      },
      '&.Mui-selected:hover': {
        backgroundColor: '#DAEEFB',
      },
    },
  },
  customeTabIndicator: {
    '& .MuiTabs-indicator': {
      width: '90px',
      borderTopLeftRadius: '20px',
      borderTopRightRadius: '20px',
      backgroundColor: '#106593',
      height: '5px',
    },
  },
}));

const DisableTooltip = styled(({ className, ...props }: TooltipProps) => (
  <Tooltip
    {...props}
    arrow
    classes={{ popper: className }}
  />
))(({ theme }) => ({
  [`& .${tooltipClasses.arrow}`]: {
    color: theme.palette.primary.main,
  },
  [`& .${tooltipClasses.tooltip}`]: {
    textAlign: 'center',
    backgroundColor: theme.palette.primary.main,
  },
}));

const Leaderboard = () => {
  const classes = useStyles();
  const summitEvent = useSelector((state: RootState) => state.user.event);
  const [stats, setStats] = useState<ByCategoryStats[]>();
  const [value, setValue] = useState('2');
  const [winnersAvailable, setWinnersAvailable] = useState(Boolean);
  const [hydraTallyAvailable, setHydraTallyAvailable] = useState(Boolean);

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
    if (env.SHOW_WINNERS === 'true') {
      setWinnersAvailable(true);
    } else {
      setWinnersAvailable(false);
    }
    if (env.SHOW_HYDRA_TALLY === 'true') {
      setHydraTallyAvailable(true);
    } else {
      setHydraTallyAvailable(false);
    }
    init();
  }, [init]);

  const statsItems: StatItem<EventPresentation['categories']>[] =
    summitEvent?.categories?.map(({ id }, index) => ({
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

  const handleChange = (event: React.SyntheticEvent, newValue: string) => {
    setValue(newValue);
  };

  const breakpointColumnsObj = {
    default: 3,
    1337: 2,
    909: 1,
  };

  const TabContextStyles = {
    container: {
      display: 'flex',
      flexDirection: 'row',
      justifyContent: 'center',
      alignItems: 'center',
      borderRadius: '40px',
      background: '#F5F9FF',
      boxShadow: '2px 2px 8px 0px rgba(67, 70, 86, 0.25)',
      width: '429px',
      height: '76px',
      margin: '0 auto',
    },
  };

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
      <Box sx={{ width: '100%', typography: 'body1', justifyContent: 'center' }}>
        <TabContext value={value}>
          <Box
            sx={TabContextStyles.container}
            className={classes.classes.customeTabIndicator}
          >
            <TabList
              onChange={handleChange}
              aria-label="lab API tabs example"
              className={classes.classes.customTab}
              textColor="primary"
              indicatorColor="primary"
            >
              <DisableTooltip title={!winnersAvailable && WinnersDisabledText}>
                {!winnersAvailable ? (
                  <span>
                    <Tab
                      icon={<WinnersIcon />}
                      label="Winners"
                      value="1"
                      disableRipple
                      disabled={!winnersAvailable}
                    />
                  </span>
                ) : (
                  <Tab
                    icon={<WinnersIcon />}
                    label="Winners"
                    value="1"
                    disableRipple
                  />
                )}
              </DisableTooltip>
              <Tab
                icon={<VotesIcon />}
                label="Total Votes"
                value="2"
                disableRipple
              />
              <DisableTooltip title={!hydraTallyAvailable && HydraTallyDisabledText}>
                {!hydraTallyAvailable ? (
                  <span>
                    <Tab
                      icon={<BarChartIcon />}
                      label="Hydra Tally"
                      value="3"
                      disableRipple
                      disabled={!hydraTallyAvailable}
                    />
                  </span>
                ) : (
                  <Tab
                    icon={<BarChartIcon />}
                    label="Hydra Tally"
                    value="3"
                    disableRipple
                  />
                )}
              </DisableTooltip>
            </TabList>
          </Box>
          <TabPanel
            value="1"
            sx={{ mx: 0, px: 0 }}
          >
            {winnersAvailable ? (
              <Masonry
                breakpointCols={breakpointColumnsObj}
                className={styles.masonryGrid}
                columnClassName={styles.masonryGridColumn}
              >
                {statsItems.map((item, index) => (
                  <AwardsTile
                    key={index}
                    title={item.label}
                    counter={index}
                    categoryId={item.id}
                  />
                ))}
              </Masonry>
            ) : (
              <Box sx={{ height: '20vw', justifyContent: 'center', textAlign: 'center' }}>
                <Typography variant="h6">
                Winners will be revealed following the Cardano Summit 2023 Gala Awards Dinner on 4 November.
                </Typography>
              </Box>
            )}
          </TabPanel>
          <TabPanel value="2">
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
          </TabPanel>
          <TabPanel
            value="3"
            sx={{ mx: 0, px: 0 }}
          >
            <Grid
              container
              justifyContent="right"
              alignItems="center"
              textAlign="center"
              m={2}
              p={2}
            >
              <Grid item xs={12}>
                <img src="/static/hydra-aiken.png"/>
              </Grid>
            </Grid>
            {hydraTallyAvailable ? (
              <Masonry
                breakpointCols={breakpointColumnsObj}
                className={styles.masonryGrid}
                columnClassName={styles.masonryGridColumn}
              >
                {statsItems.map((item, index) => (
                  <HydraTile
                    key={index}
                    title={item.label}
                    counter={index}
                    categoryId={item.id}
                  />
                ))}
              </Masonry>
            ) : (
              <Box sx={{ height: '20vw', justifyContent: 'center', textAlign: 'center' }}>
                <Typography variant="h6">
                Hydra Tally results will be available on-chain following the Cardano Summit 2023 Awards Gala Dinner on 4 November.
                </Typography>
              </Box>
            )}
          </TabPanel>
        </TabContext>
      </Box>
    </div>
  );
};

export { Leaderboard };
