import React from 'react';
import Card from '@mui/material/Card';
import CardContent from '@mui/material/CardContent';
import Typography from '@mui/material/Typography';
import styles from './AwardsTile.module.scss';
import { Grid } from '@mui/material';

const AwardsTile = ({ title, children }) => {
  return (
    <Card className={styles.awardCard}>
      <CardContent>
        <Grid
          container
          spacing={0}
          direction="column"
          gap="10px"
        >
          <Grid item>
            <Typography
              variant="h5"
              component="div"
              className={styles.awardTitle}
            >
              {title}
            </Typography>
          </Grid>
          <Grid item>
            {children}
          </Grid>
        </Grid>
      </CardContent>
    </Card>
  );
};

export { AwardsTile };
