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
      {privacyData.description.map((paragraph, index) => (
        <Typography
          key={index}
          variant="body1"
          sx={{ mt: 4 }}
        >
          {paragraph}
        </Typography>
      ))}

      {privacyData.sections.map((section, index) => {
        return (
          <div key={`${section.title}-${index}`}>
            <Typography
              variant="h5"
              sx={{ mt: 5, fontWeight: 'bold' }}
            >
              {section.title}
            </Typography>
            {section.subsections &&
              section.subsections.map((subsection, subIndex) => (
                <div key={`${subsection.title}-${subIndex}`}>
                  <Typography
                    variant="h6"
                    sx={{ mt: 4, fontWeight: 'bold' }}
                  >
                    {subsection.title}
                  </Typography>

                  {subsection.content.map((paragraph, paragraphIndex) => (
                    <Typography
                      key={paragraphIndex}
                      variant="body1"
                      sx={{ mt: 1 }}
                      dangerouslySetInnerHTML={{ __html: paragraph }}
                    />
                  ))}

                  {subsection.extras && (
                    <div>
                      {Object.entries(subsection.extras).map(([extra, text], termIndex) => (
                        <Typography
                          key={extra}
                          variant="body1"
                          sx={{ mt: 1, ml: 4 }}
                          dangerouslySetInnerHTML={{ __html: text }}
                        />
                      ))}
                    </div>
                  )}
                </div>
              ))}
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
