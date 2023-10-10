import React, { useCallback, useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import Card from '@mui/material/Card';
import CardContent from '@mui/material/CardContent';
import * as leaderboardService from '../../../../common/api/leaderboardService';
import { eventBus } from 'utils/EventBus';
import SUMMIT2023CONTENT from '../../../../common/resources/data/summit2023Content.json';
import { ProposalContent } from 'pages/Nominees/Nominees.type';
import { CategoryContent } from 'pages/Categories/Category.types';
import styles from './AwardsTile.module.scss';
import { Box, Button, CardActions, Grid, Typography } from '@mui/material';
import cn from 'classnames';

const AwardsTile = ({ title, categoryId }) => {
  const summit2023Category: CategoryContent = SUMMIT2023CONTENT.categories.find(
    (category) => category.id === categoryId
  );
  const summit2023Proposals: ProposalContent[] = summit2023Category.proposals;
  const [awards, setAwards] = useState([]);

  const init = useCallback(async () => {
    try {
      await leaderboardService.getCategoryLevelStats(categoryId).then((response) => {
        const updatedAwards = summit2023Proposals.map((proposal) => {
          const id = proposal.id;
          const votes = response?.proposals[id] ? response?.proposals[id].votes : 0;
          return { ...proposal, votes };
        });

        updatedAwards.sort((a, b) => b.votes - a.votes);
        setAwards(updatedAwards);
      });
    } catch (error) {
      const message = `Failed to fecth Nominee stats: ${error?.message || error?.toString()}`;
      if (process.env.NODE_ENV === 'development') {
        console.log(message);
      }
      eventBus.publish('showToast', 'Failed to fecth Nominee stats', 'error');
    }
  }, []);

  useEffect(() => {
    init();
  }, [init]);

  return (
    <div>
      <Card
        className={styles.awardCard}
        key={categoryId}
      >
        <CardContent>
          {awards.length > 0 && (
            <Grid
            container
            spacing={0}
            direction="row"
            sx={{ marginTop: '25px', justifyContent: 'center' }}
          >
            <Grid
              container
              sx={{ justifyContent: 'center' }}
            >
              <Card
                key={awards[1].id}
                variant="outlined"
                className={styles.rankCardNext}
              >
                <CardContent sx={{ p: '11px' }}>
                  <Box><img src="/static/second_place.png"  style={{width: 64, height: 64}}/></Box>
                  <p className={styles.rankTitle}>{awards[1].presentationName}</p>
                  <p>{awards[1].votes} Votes</p>
                </CardContent>
              </Card>
              <Card
                key={awards[0].id}
                variant="outlined"
                className={styles.rankCard}
              >
                <CardContent sx={{ p: '11px' }}>
                  <Box><img src="/static/first_place.png"  style={{width: 94, height: 94}}/></Box>
                  <p className={styles.rankTitle}>{awards[0].presentationName}</p>
                  <p>{awards[0].votes} Votes</p>
                </CardContent>
              </Card>
              <Card
                key={awards[2].id}
                variant="outlined"
                className={styles.rankCardNext}
              >
                <CardContent sx={{ p: '11px' }}>
                  <Box><img src="/static/third_place.png"  style={{width: 64, height: 64}}/></Box>
                  <p className={styles.rankTitle}>{awards[3].presentationName}</p>
                  <p>{awards[3].votes} Votes</p>
                </CardContent>
              </Card>
            </Grid>
          </Grid>
          )}
          
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
                Rank
              </Typography>
              <Typography
                variant="h5"
                className={styles.optionTitle}
              >
                Nominee
              </Typography>
              <Typography
                variant="h5"
                className={styles.optionTitle}
              >
                Votes
              </Typography>
            </Grid>
            {awards.slice(3).map((proposal, index) => (
              <React.Fragment key={index}>
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
                    {index + 4}
                  </Typography>
                  <Typography
                    variant="h5"
                    className={cn(styles.optionTitle, styles.statTitle)}
                  >
                    {proposal.presentationName}
                  </Typography>
                  <Typography
                    variant="h5"
                    className={cn(styles.optionTitle, styles.statTitle)}
                  >
                    {proposal.votes}
                  </Typography>
                </Grid>
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
              View All Nominees
            </Button>
          </CardActions>
        </CardContent>
      </Card>
    </div>
  );
};

export { AwardsTile };
