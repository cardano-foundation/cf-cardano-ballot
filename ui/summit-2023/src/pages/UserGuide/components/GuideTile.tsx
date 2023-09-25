import React from 'react';
import { Card, CardContent, CardMedia, Typography } from '@mui/material';
import styles from './GuideTile.module.scss';

type GuideTilePorps = {
  width?: string;
  height: number;
  media: string;
  graphic: any | React.ReactElement;
  stepNumber: any | React.ReactElement;
  stepTitle: string | React.ReactElement;
  stepHint?: string | React.ReactElement;
};

export const GuideTile = ({ width, height, media, graphic, stepNumber, stepTitle, stepHint }: GuideTilePorps) => {
  return (
    <Card
      className={styles.guideCard}
      sx={{ width: {width}, height: { height } }}
    >
      {media === 'video' ? (
        <CardMedia
          component="video"
          src={graphic}
          sx={{ objectFit: 'cover' }}
          autoPlay
          controls
        />
      ) : (
        <CardMedia
          component="img"
          height="260"
          image={graphic}
          sx={{ objectFit: 'cover' }}
        />
      )}
      <CardContent>
        {stepNumber}
        <Typography
          gutterBottom
          variant="h5"
          className={styles.guideTitle}
          component="div"
        >
          {stepTitle}
        </Typography>
        {stepHint && (
          <Typography
            variant="body2"
            className={styles.guideSummary}
            color="text.secondary"
          >
            {stepHint}
          </Typography>
        )}
      </CardContent>
    </Card>
  );
};
