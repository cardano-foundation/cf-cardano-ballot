import React from 'react';
import { Box, Card, CardContent, CardMedia, Typography } from '@mui/material';
import styles from './GuideTile.module.scss';

type GuideTilePorps = {
  width: number;
  height: number;
  graphic: any | React.ReactElement;
  featureImg?: any | React.ReactElement;
  stepNumber: any | React.ReactElement;
  stepTitle: string | React.ReactElement;
  stepHint?: string | React.ReactElement;
  featureImgStyle?: any;
};

export const GuideTile = ({ width, height, featureImg, graphic, stepNumber, stepTitle, stepHint, featureImgStyle }: GuideTilePorps) => {
  return (
    <Card
      className={styles.guideCard}
      sx={{ width: { width }, height: { height } }}
    >
      <CardMedia
        component="img"
        height="260"
        image={graphic}
        alt=""
        sx={{objectFit: 'fill'}}
      />
      <Box
        sx={{
          position: 'absolute',
          display: 'flex',
          justifyContent: 'center',
          alignItems: 'center',
          margin: '0 auto',
          width: 'fit-content',
          verticalAlign: 'middle'
        }}
      >
        <img style={featureImgStyle} src={featureImg} />
      </Box>
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
