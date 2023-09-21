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
        {/* Terms and Conditions 1 */}
        <Typography
          variant="h3"
          sx={{ mb: 4, mt: 4 }}
        >
          {termsData.title}
        </Typography>
        <Typography
          variant="body1"
          sx={{ mb: 5, mt: 2 }}
        >
          {termsData.date}
        </Typography>
        {termsData.sections.map((section, index) => {
          return (
            <div key={`${section.title}-${index}`}>
              <Typography
                variant="h5"
                sx={{ mt: 5, fontWeight: 'bold' }}
              >
                {section.title}
              </Typography>
              {section.content &&
                section.content.map((paragraph, paragraphIndex) => (
                  <Typography
                    key={paragraphIndex}
                    variant="body1"
                    sx={{ mt: 1 }}
                    dangerouslySetInnerHTML={{ __html: paragraph }}
                  />
                ))}
              {section.subsections &&
                section.subsections.map((subsection, subIndex) => (
                  <div key={`${subsection.title}-${subIndex}`}>
                    <Typography
                      variant="h6"
                      sx={{ mt: 4, fontWeight: 'bold' }}
                    >
                      {subsection.title}
                    </Typography>

                    <Typography
                      variant="body1"
                      sx={{ mt: 1 }}
                    >
                      {subsection.content}
                    </Typography>

                    {subsection.definitions && (
                      <div>
                        {Object.entries(subsection.definitions).map(([definition, text], termIndex) => (
                          <Typography
                            key={definition}
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
        {/* Terms and Conditions 2 */}
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 20 }}>
          <Typography
            variant="h3"
            sx={{ mb: 4, mt: 4 }}
          >
            {termsData.title}
          </Typography>
        </div>
        <Typography
          variant="body1"
          sx={{ mb: 5, mt: 2 }}
        >
          {termsData.date}
        </Typography>
        {termsData.terms.map((term, index) => (
          <div key={`${term.title}-${index}`}>
            <Typography
              variant="h6"
              sx={{ mt: 4, fontWeight: 'bold' }}
            >
              {term.title}
            </Typography>
            {term.list.map((item, indexTerm) => (
              <div key={`${term.title}-${indexTerm}`}>
                {item.content.map((paragraph, indexParagraph) => (
                  <Typography
                    key={`${item.number}-${indexParagraph}`}
                    variant="body1"
                    sx={{ mt: 3 }}
                    dangerouslySetInnerHTML={{ __html: paragraph }}
                  />
                ))}
              </div>
            ))}
          </div>
        ))}

        <Typography
          variant="h6"
          sx={{ mt: 4, fontWeight: 'bold' }}
        >
          {termsData.disclaimer.title}
        </Typography>
        <Typography
          variant="body1"
          sx={{ mt: 1 }}
        >
          {termsData.disclaimer.content}
        </Typography>
        <Typography
          variant="h6"
          sx={{ mt: 4, fontWeight: 'bold' }}
        >
          {termsData.liability.title}
        </Typography>
        <Typography
          variant="body1"
          sx={{ mt: 1 }}
        >
          {termsData.liability.content}
        </Typography>
        <Typography
          variant="h6"
          sx={{ mt: 4, fontWeight: 'bold' }}
        >
          {termsData.miscellaneous.title}
        </Typography>
        {termsData.miscellaneous.list.map((mis, index) => {
          return (
            <div key={index}>
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
          variant="h6"
          sx={{ mt: 4, fontWeight: 'bold' }}
        >
          {termsData.contactus.title}
        </Typography>
        <Typography
          variant="body1"
          sx={{ mt: 1 }}
          dangerouslySetInnerHTML={{ __html: termsData.contactus.content }}
        />

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
