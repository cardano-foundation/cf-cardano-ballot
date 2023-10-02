import React from 'react';
import { Box, Card, CardContent, CardMedia, Typography } from '@mui/material';
import styles from './GuideTile.module.scss';
import { eventBus } from 'utils/EventBus';
import { NavLink } from 'react-router-dom';

type GuideTilePorps = {
  width?: string;
  height?: number;
  media: string;
  graphic: any | React.ReactElement;
  stepNumber: any | React.ReactElement;
  stepTitle: string | React.ReactElement;
  stepHint?: string | React.ReactElement;
  link?: string | React.ReactElement;
};

export const GuideTile = ({ width, height, media, graphic, stepNumber, stepTitle, stepHint, link }: GuideTilePorps) => {
  return (
    <Card
      className={styles.guideCard}
      sx={{ width: { width }, height: { height } }}
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
        <Box sx={{ my: 1 }}>{stepNumber}</Box>
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
            {stepHint}{' '}
            {link && (
              <NavLink
                onClick={() => {
                  eventBus.publish('openConnectWalletModal');
                }}
                to={''}
                className={styles.link}
              >
                {link}
              </NavLink>
            )}
          </Typography>
        )}
      </CardContent>
    </Card>
  );
};
