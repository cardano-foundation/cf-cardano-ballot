import React from 'react';
import Container from '@mui/material/Container';
import privacyData from '../../../common/resources/data/privacyPolicy.json';
import { Button, Grid, Typography } from '@mui/material';
import { Link as RouterLink } from 'react-router-dom';
import HomeIcon from '@mui/icons-material/Home';

const PrivacyPolicy = () => {
  return (
    <Container>
      {/* Privacy Policy */}
      <Typography
        variant="h3"
        sx={{ mb: 4, mt: 4 }}
      >
        {privacyData.title}
      </Typography>
      <Typography
        variant="body1"
        sx={{ mb: 5, mt: 2 }}
      >
        {privacyData.date}
      </Typography>
      <Typography
        variant="body1"
        sx={{ mt: 4 }}
      >
        {privacyData.description}
      </Typography>
      <Typography
        variant="body2"
        sx={{ mt: 2 }}
      >
        {privacyData.subdescription}
      </Typography>

      {privacyData.sections.map((section, index) => {
        return (
          <div key={`${section.title}-${index}`}>
            <Typography
              variant="subtitle2"
              sx={{ mt: 1 }}
            >
              {section.title}
            </Typography>
            <Typography variant="body1">{section.content}</Typography>
          </div>
        );
      })}

      <Grid
        item
        xs="auto"
        sx={{ mt: 2, textAlign: 'center' }}
      >
        <Button
          color="primary"
          component={RouterLink}
          to="/"
          variant="outlined"
          startIcon={<HomeIcon />}
        >
          Back to Home
        </Button>
      </Grid>
    </Container>
  );
};

export { PrivacyPolicy };
