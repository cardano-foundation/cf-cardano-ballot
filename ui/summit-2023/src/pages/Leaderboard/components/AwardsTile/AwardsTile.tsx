import React, { useCallback, useEffect, useState } from 'react';
import { Avatar, Box, Button, CardActions, Chip, CircularProgress, Grid, Typography } from '@mui/material';
import { Link } from 'react-router-dom';
import Card from '@mui/material/Card';
import { i18n } from 'i18n';
import CardContent from '@mui/material/CardContent';
import * as leaderboardService from '../../../../common/api/leaderboardService';
import { eventBus } from 'utils/EventBus';
import SUMMIT2023CONTENT from '../../../../common/resources/data/summit2023Content.json';
import { ProposalContent } from 'pages/Nominees/Nominees.type';
import { CategoryContent } from 'pages/Categories/Category.types';
import styles from './AwardsTile.module.scss';
import cn from 'classnames';
import CATEGORY_IMAGES from '../../../../common/resources/data/categoryImages.json';
import { setWinners } from '../../../../store/userSlice';
import { useDispatch } from 'react-redux';

const AwardsTile = ({ counter, title, categoryId }) => {
  const summit2023Category: CategoryContent = SUMMIT2023CONTENT.categories.find(
    (category) => category.id === categoryId
  );
  const summit2023Proposals: ProposalContent[] = summit2023Category.proposals;
  const [awards, setAwards] = useState([]);
  const [loaded, setLoaded] = useState(false);
  const dispatch = useDispatch();
  
  const init = useCallback(async () => {
    try {
      await leaderboardService.getCategoryLevelStats(categoryId).then((response) => {
        const updatedAwards = summit2023Proposals.map((proposal) => {
          const id = proposal.id;
          const votes = response?.proposals[id] ? response?.proposals[id].votes : 0;
          const rank = 0;
          return { ...proposal, votes, rank, categoryId};
        });

        updatedAwards.sort((a, b) => b.votes - a.votes);

        updatedAwards.forEach((item, index, array) => {
          if (index > 0 && item.votes === array[index - 1].votes) {
            item.rank = array[index - 1].rank;
          } else {
            item.rank = index + 1;
          }
        });
        setAwards(updatedAwards);

        const categoryWinners = updatedAwards.map((winner) => {
          if(winner.rank === 1) {
            const proposalId = winner.id;
            return { categoryId, proposalId};
          }
        });

        dispatch(setWinners({ winners: categoryWinners }));

      });
      setLoaded(true);
    } catch (error) {
      const message = `Failed to fecth Nominee stats: ${error?.message || error?.toString()}`;
      if (process.env.NODE_ENV === 'development') {
        console.log(message);
      }
      eventBus.publish('showToast', i18n.t('toast.failedToFecthNomineeStats'), 'error');
    }
  }, []);

  useEffect(() => {
    init();
  }, [init]);

  return (
    <div data-testid="award-tile">
      {loaded ? (
        <Card
          className={styles.awardCard}
          key={categoryId}
          sx={{ width: '100%' }}
        >
          <CardContent>
            <Chip
              avatar={
                <Avatar
                  alt={title}
                  src={CATEGORY_IMAGES[counter]}
                />
              }
              color="default"
              label={title}
              className={styles.awardTitle}
              variant="filled"
            />
            {awards.length > 0 && (
              <Grid
                container
                spacing={0}
                direction="row"
                sx={{ marginTop: '15px', justifyContent: 'center' }}
              >
                <Grid container>
                  {awards.slice(0, 2).map((proposal, index) => (
                    <Grid
                      item
                      xs={12}
                      key={index}
                    >
                      {proposal.rank === 1 && (
                        <Card
                          key={index}
                          variant="outlined"
                          className={styles.rankCard}
                        >
                          <Box sx={{ display: 'flex', flexDirection: 'row' }}>
                            <Box className={styles.trophy}>
                              <img
                                src="/static/wwcd.svg"
                                style={{ width: 54, height: 60 }}
                              />
                            </Box>
                            <CardContent sx={{ flex: '1 0 auto' }}>
                              <Typography
                                component="div"
                                variant="h6"
                                className={styles.title}
                              >
                                {proposal.presentationName}
                              </Typography>
                              <Typography
                                variant="subtitle1"
                                color="text.secondary"
                                component="div"
                              >
                                {proposal.votes} {i18n.t('leaderboard.tabs.tab1.tile.votesLabel')}
                              </Typography>
                            </CardContent>
                          </Box>
                        </Card>
                      )}
                    </Grid>
                  ))}
                </Grid>
              </Grid>
            )}

            <Grid
              container
              spacing={0}
              direction="column"
              justifyContent="space-between"
              sx={{ marginTop: '25px' }}
            >
              <Grid
                container
              >
                <Grid
                  item
                  xs={3}
                  textAlign="left"
                >
                  <Typography
                    variant="h5"
                    className={styles.listTitle}
                  >
                    {i18n.t('leaderboard.tabs.tab1.tile.tableHeadings.column1')}
                  </Typography>
                </Grid>
                <Grid
                  item
                  xs={7}
                  textAlign="left"
                >
                  <Typography
                    variant="h5"
                    textAlign="left"
                    className={styles.listTitle}
                  >
                    {i18n.t('leaderboard.tabs.tab1.tile.tableHeadings.column2')}
                  </Typography>
                </Grid>
                <Grid
                  item
                  xs={2}
                  textAlign="right"
                >
                  <Typography
                    variant="h5"
                    className={styles.listTitle}
                  >
                    {i18n.t('leaderboard.tabs.tab1.tile.tableHeadings.column3')}
                  </Typography>
                </Grid>
              </Grid>
              {awards.map((proposal, index) => (
                <React.Fragment key={index}>
                  <div className={styles.divider} />
                  {proposal.rank !== 1 && (
                    <Grid
                      container
                      data-testid="total-stats-item"
                      sx={{ my: '15px' }}
                    >
                      <Grid
                        item
                        xs={3}
                        textAlign="left"
                      >
                        <Typography
                          variant="h5"
                          className={cn(styles.optionTitle, styles.statTitle)}
                        >
                          {proposal.rank}
                        </Typography>
                      </Grid>
                      <Grid
                        item
                        xs={7}
                        textAlign="left"
                      >
                        <Typography
                          variant="h5"
                          className={cn(styles.optionTitle, styles.statTitle)}
                        >
                          {proposal.presentationName}
                        </Typography>
                      </Grid>
                      <Grid
                        item
                        xs={2}
                        textAlign="right"
                      >
                        <Typography
                          variant="h5"
                          className={cn(styles.optionTitle, styles.statTitle)}
                        >
                          {proposal.votes}
                        </Typography>
                      </Grid>
                    </Grid>
                  )}
                </React.Fragment>
              ))}
            </Grid>
            <CardActions>
              <Button
                component={Link}
                to={{ pathname: `/nominees/${categoryId}` }}
                aria-label="View Nominees"
                variant="contained"
                size="large"
                sx={{
                  color: 'text.primary',
                  fontSize: 16,
                  fontWeight: 700,
                  textTransform: 'none',
                  width: '100%',
                  backgroundColor: '#acfcc5 !important',
                }}
              >
                {i18n.t('button.viewAllNominees')}
              </Button>
            </CardActions>
          </CardContent>
        </Card>
      ) : (
        <Box
          sx={{
            display: 'flex',
            height: '60vh',
            alignItems: 'center',
            justifyContent: 'center',
          }}
        >
          <CircularProgress
            className="app-spinner"
            style={{
              color: '#03021f',
              strokeWidth: '10',
            }}
          />
        </Box>
      )}
    </div>
  );
};

export { AwardsTile };