import React from 'react';
import Grid from '@mui/material/Grid';
import { Typography, Button, Container, IconButton } from '@mui/material';
import CloseIcon from '@mui/icons-material/Close';
import xIcon from '../../common/resources/images/x-icon.svg';
import linkedinIcon from '../../common/resources/images/linkedin-icon.svg';
import gitHubIcon from '../../common/resources/images/github-icon.svg';

const ReadMore = (props) => {
  const { nominee, closeSidePage } = props;

  const handleClose = () => {
    closeSidePage(false);
  };

  const shouldDisplayGrid = nominee.urls.some(
    (url) => url.includes('twitter.com') || url.includes('linkedin.com') || url.includes('github.com')
  );

  return (
    <>
      <Grid
        container
        p={1}
      >
        <Grid
          item
          xs={11}
        />
        <Grid
          item
          xs={1}
        >
          <IconButton
            className="closeButton"
            onClick={() => handleClose()}
            aria-label="close"
            style={{ float: 'right' }}
          >
            <CloseIcon />
          </IconButton>
        </Grid>
      </Grid>
      <Container style={{ margin: '5px' }}>
        <Typography
          variant="h5"
          gutterBottom
          className="nominee-slide-title"
        >
          {nominee.presentationName}
        </Typography>

        {shouldDisplayGrid ? (
          <Grid
            container
            spacing={1}
            marginTop={1}
            marginBottom={2}
          >
            {nominee.urls.map((url, index) => (
              <Grid
                item
                key={index}
              >
                {url.includes('twitter.com') ? (
                  <Grid item>
                    <IconButton
                      className="nominee-social-button"
                      aria-label="X"
                      href={url}
                    >
                      <img
                        src={xIcon}
                        alt="X"
                        style={{ width: '20px' }}
                      />
                    </IconButton>
                  </Grid>
                ) : null}
                {url.includes('linkedin.com') ? (
                  <Grid item>
                    <IconButton
                      className="nominee-social-button"
                      aria-label="Linkedin"
                      href={url}
                    >
                      <img
                        src={linkedinIcon}
                        alt="Linkedin"
                        style={{ width: '20px' }}
                      />
                    </IconButton>
                  </Grid>
                ) : null}
                {url.includes('github.com') ? (
                  <Grid item>
                    <IconButton
                      className="nominee-social-button"
                      aria-label="GitHub"
                      href={url}
                    >
                      <img
                        src={gitHubIcon}
                        alt="GitHub"
                        style={{ width: '20px' }}
                      />
                    </IconButton>
                  </Grid>
                ) : null}
              </Grid>
            ))}
          </Grid>
        ) : null}

        <Typography
          variant="body2"
          paragraph
          style={{ maxWidth: '490px' }}
          className="nominee-slide-description"
        >
          {nominee.desc}
        </Typography>

        {!shouldDisplayGrid ? (
          <>
            {nominee.urls.map((url, index) => (
              <Button
                key={index}
                className="visit-web-button"
                href={url}
                fullWidth
              >
                {url}
              </Button>
            ))}
          </>
        ) : null}
      </Container>
    </>
  );
};

export default ReadMore;
