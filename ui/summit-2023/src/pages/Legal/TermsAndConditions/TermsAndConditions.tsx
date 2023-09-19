import React from 'react';
import Container from '@mui/material/Container';
import styles from './TermsAndConditions.module.scss';
import { Button, Grid, Typography } from '@mui/material';
import termsData from '../../../common/resources/data/termsAndConditions.json';
import { Link as RouterLink } from 'react-router-dom';
import HomeIcon from '@mui/icons-material/Home';

const TermsAndConditions = () => {
  return (
    <div
      data-testid="termsandconditions-page"
      className={styles.termsandconditions}
    >
      <Container>
        {/* Terms and Conditions */}
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 20 }}>
          <Typography
            className={styles.title}
            variant="h2"
            fontSize={{
              xs: '28px',
              md: '32px',
            }}
            lineHeight={{
              xs: '28px',
              md: '32px',
            }}
          >
            {termsData.title}
          </Typography>
        </div>
        <Typography
          variant="body1"
          sx={{ mb: 1, mt: 2 }}
        >
          {termsData.date}
        </Typography>
        <Typography
          variant="subtitle1"
          sx={{ mt: 4 }}
        >
          {termsData.subtitle}
        </Typography>
        {termsData.sections.map((section, index) => {
          return (
            <div key={`${section.title}-${index}`}>
              <Typography
                variant="subtitle2"
                sx={{ mt: 4 }}
              >
                {section.title}
              </Typography>
              <Typography variant="body1">{section.content}</Typography>
            </div>
          );
        })}
        {termsData.terms.map((term, index) => {
          return (
            <div key={`${term.title}-${index}`}>
              <Typography
                variant="subtitle2"
                sx={{ mt: 4 }}
              >
                {term.title}
              </Typography>
              {term.list.map((t) => {
                return (
                  <Typography
                    variant="body1"
                    key={t.number}
                  >
                    {t.number} {t.content}
                  </Typography>
                );
              })}
            </div>
          );
        })}

        <Typography
          variant="subtitle2"
          sx={{ mt: 1 }}
        >
          {termsData.disclaimer.title}
        </Typography>
        <Typography variant="body1">{termsData.disclaimer.content}</Typography>

        <Typography
          variant="subtitle2"
          sx={{ mt: 1 }}
        >
          {termsData.liability.title}
        </Typography>
        <Typography variant="body1">{termsData.liability.content}</Typography>

        <Typography
          variant="subtitle2"
          sx={{ mt: 1 }}
        >
          {termsData.miscellaneous.title}
        </Typography>
        {termsData.miscellaneous.list.map((mis) => {
          return (
            <div key={mis.number}>
              <Typography
                variant="body1"
                sx={{ mt: 1 }}
              >
                {mis.number} {mis.content}
              </Typography>
            </div>
          );
        })}

        <Typography
          variant="subtitle2"
          sx={{ mt: 1 }}
        >
          {termsData.contactus.title}
        </Typography>
        <Typography variant="body1">{termsData.contactus.content}</Typography>

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
    </div>
  );
};

export { TermsAndConditions };
