import React, { useCallback, useEffect, useState } from 'react';
import { Avatar, Box, Button, CardActions, Chip, CircularProgress, Grid, Typography } from '@mui/material';
import { Link } from 'react-router-dom';
import Card from '@mui/material/Card';
import CardContent from '@mui/material/CardContent';
import { eventBus } from 'utils/EventBus';
import SUMMIT2023CONTENT from '../../../../common/resources/data/summit2023Content.json';
import { ProposalContent } from 'pages/Nominees/Nominees.type';
import { CategoryContent } from 'pages/Categories/Category.types';
import styles from './HydraTile.module.scss';
import cn from 'classnames';
import { i18n } from 'i18n';
import CATEGORY_IMAGES from '../../../../common/resources/data/categoryImages.json';
import { CustomButton } from 'components/common/Button/CustomButton';

const HydraTile = ({ counter, title, categoryId, hydraTallyStats }) => {
  const summit2023Category: CategoryContent = SUMMIT2023CONTENT.categories.find(
    (category) => category.id === categoryId
  );
  const summit2023Proposals: ProposalContent[] = summit2023Category.proposals;
  const [awards, setAwards] = useState([]);
  const [loaded, setLoaded] = useState(false);
  const [categoryResultsDatum, setCategoryResultsDatum] = useState('');

  const init = useCallback(async () => {
    try {
      const categoryStats = hydraTallyStats?.find((category) => category.categoryId === categoryId)
      
      const updatedAwards = summit2023Proposals.map((proposal) => {
        const id = proposal.id;
        const votes = categoryStats?.results[id] ? categoryStats?.results[id] : 0;
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

      setCategoryResultsDatum(categoryStats.metadata.categoryResultsDatum);

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
    <div data-testid="hydra-tally-tile">
      {loaded ? (
        <Card
          className={styles.hydraCard}
          key={categoryId}
          sx={{width: '100% !important'}}
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
              className={styles.hydraTallyTitle}
              variant="filled"
            />

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
                  className={styles.listTitle}
                >
                  {i18n.t('leaderboard.tabs.tab3.tile.tableHeadings.column1')}
                </Typography>
                <Typography
                  variant="h5"
                  className={styles.listTitle}
                >
                  {i18n.t('leaderboard.tabs.tab3.tile.tableHeadings.column2')}
                </Typography>
              </Grid>
              {awards.slice(0, 2).map((proposal, index) => (
                <React.Fragment key={index}>
                  {(proposal.rank === 1 && proposal.votes > 0 ) && (
                    <Grid
                      container
                      justifyContent="space-between"
                      data-testid="total-stats-item"
                      sx={{ my: '15px' }}
                    >
                      <Typography
                        variant="h5"
                        sx={{width: 200}}
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
                  )}
                </React.Fragment>
              ))}
            </Grid>
            <CardActions>
              <CustomButton
                styles={{
                  background: 'transparent !important',
                  color: '#03021F',
                  border: '1px solid #daeefb',
                  width: '100%',
                }}
                label={i18n.t('leaderboard.tabs.tab3.tile.datumInspectorButton')}
                onClick={() => window.location.href = `https://cardanoscan.io/datumInspector?datum=${categoryResultsDatum}`}
                fullWidth={true}
              />
            </CardActions>
            <CardActions>
              <Button
                component={Link}
                to={{ pathname: `/nominees/${categoryId}` }}
                aria-label="View All Nominees"
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

export { HydraTile };
