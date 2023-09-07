import React from 'react';
import Grid from '@mui/material/Grid';
import { Typography, Button, Container, IconButton } from '@mui/material';
import CloseIcon from '@mui/icons-material/Close';
import xIcon from '../../common/resources/images/x-icon.svg';
import linkedinIcon from '../../common/resources/images/linkedin-icon.svg';

const ReadMore = (props) => {
  const { nominee, closeSidePage } = props;

  const handleClose = () => {
    closeSidePage(false);
  };

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

        <Grid
          container
          spacing={1}
          marginTop={1}
          marginBottom={2}
        >
          <Grid item>
            <IconButton
              className="nominee-social-button"
              aria-label="X"
            >
              <img
                src={xIcon}
                alt="X"
                style={{ width: '20px' }}
              />
            </IconButton>
          </Grid>
          <Grid item>
            <IconButton
              className="nominee-social-button"
              aria-label="Linkedin"
            >
              <img
                src={linkedinIcon}
                alt="Linkedin"
                style={{ width: '20px' }}
              />
            </IconButton>
          </Grid>
        </Grid>

        <Typography
          variant="body2"
          paragraph
          style={{ maxWidth: '490px' }}
          className="nominee-slide-description"
        >
          {nominee.desc}
        </Typography>

        <Button
          className="visit-web-button"
          href={nominee.url}
          fullWidth
        >
          {nominee.url}
        </Button>
      </Container>
    </>
  );
};

export default ReadMore;
