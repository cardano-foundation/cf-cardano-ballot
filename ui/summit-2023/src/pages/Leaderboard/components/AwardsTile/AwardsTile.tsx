import React, { useCallback, useEffect, useState } from 'react';
import { Avatar, Box, Button, CardActions, Chip, CircularProgress, Grid, Typography } from '@mui/material';
import { Link } from 'react-router-dom';
import Card from '@mui/material/Card';
import { i18n } from 'i18n';
import CardContent from '@mui/material/CardContent';
import { eventBus } from 'utils/EventBus';
import SUMMIT2023CONTENT from '../../../../common/resources/data/summit2023Content.json';
import { ProposalContent } from 'pages/Nominees/Nominees.type';
import { CategoryContent } from 'pages/Categories/Category.types';
import styles from './AwardsTile.module.scss';
import cn from 'classnames';
import CATEGORY_IMAGES from '../../../../common/resources/data/categoryImages.json';
const AwardsTile = ({ counter, title, categoryId, votingResults }) => {
  const summit2023Category: CategoryContent = SUMMIT2023CONTENT.categories.find(
    (category) => category.id === categoryId
  );
  const summit2023Proposals: ProposalContent[] = summit2023Category.proposals;
  const [awards, setAwards] = useState([]);
  const [loaded, setLoaded] = useState(false);
  
  const init = useCallback(async () => {
    try {
      const categoryResults = votingResults?.find((category) => category.category === categoryId);
      
      const updatedAwards = summit2023Proposals.map((proposal) => {
        const id = proposal.id;
        const votes = categoryResults?.proposals[id] ? categoryResults?.proposals[id].votes : 0;
        const rank = 0;
        return { ...proposal, votes, rank };
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
      setLoaded(true);
    } catch (error) {
      const message = `Failed to fetch Nominee stats: ${error?.message || error?.toString()}`;
      if (process.env.NODE_ENV === 'development') {
        console.log(message);
      }
      eventBus.publish('showToast', i18n.t('toast.failedToFetchNomineeStats'), 'error');
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
                  sx={{filter: 'opacity(0.7)'}}
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
                      {(proposal.rank === 1 && proposal.votes > 0 ) && (
                        <Card
                          key={index}
                          variant="outlined"
                          className={styles.rankCard}
                        >
                          <Box sx={{ display: 'flex', flexDirection: 'row' }}>
                            <Box className={styles.trophy}>
                              <img
                                src="/static/cardano-summit-award.png"
                                style={{ width: 'auto', height: 94 }}
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
                  fontWeight: 600,
                  textTransform: 'none',
                  width: '100%',
                  backgroundColor: '#acfcc5 !important',
                  justifyContent: 'center',
                  alignItems: 'center',
                  gap: '10px',
                  borderRadius: '8px',
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