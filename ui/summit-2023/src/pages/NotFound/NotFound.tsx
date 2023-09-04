import React from 'react';
import { Box, useTheme, useMediaQuery, Typography, Button } from '@mui/material';
import { useNavigate } from 'react-router-dom';
import { ROUTES } from '../../routes';
import './NotFound.scss';
import errorImage from '../../common/resources/images/404-error.svg';

const NotFound = () => {
  const navigate = useNavigate();
  const theme = useTheme();
  const isTablet = useMediaQuery(theme.breakpoints.down('lg'));

  return (
    <>
      <Box
        px={20}
        sx={{
          display: 'flex',
          flexDirection: { xs: 'column', sm: 'row' },
          height: { xs: 'auto', sm: '400px' },
          paddingLeft: isTablet ? '0px' : '160px',
          paddingRight: isTablet ? '0px' : '160px',
        }}
      >
        <Box
          sx={{
            flex: '1',
            padding: '20px',
            marginRight: isTablet ? '0px' : '40px',
            marginTop: !isTablet ? '10%' : '0px',
            order: { xs: '1', sm: '1' },
          }}
        >
          <Typography
            className="nominees-title"
            variant="h4"
            sx={{
              display: 'flex',
              justifyContent: isTablet ? 'center' : 'start',
            }}
          >
            Page Not Found
          </Typography>
          <Typography
            className="nominees-description"
            variant="body1"
            gutterBottom
            sx={{
              display: 'flex',
              justifyContent: isTablet ? 'center' : 'start',
              marginTop: isTablet ? '60px' : '40px',
              width: isTablet ? 'auto' : '550px',
              wordBreak: 'break-word',
            }}
          >
            Sorry, but it seems the page you're searching for doesn’t exist. Please feel free to click the button below
            and return to the home page, or you can use the navigation located at the top of the page to find your way
            around.
          </Typography>
          <Box
            sx={{
              display: 'flex',
              justifyContent: isTablet ? 'center' : 'start',
              marginTop: isTablet ? '0px' : '40px',
            }}
          >
            <Button
              className="go-home-button"
              style={{ width: 'auto' }}
              onClick={() => navigate(ROUTES.INTRO)}
            >
              Go home
            </Button>
          </Box>
        </Box>

        {!isTablet ? (
          <Box
            sx={{
              flex: '1',
              padding: '20px',
              order: { xs: '2', sm: '2' },
            }}
          >
            <img
              style={{ width: '450px' }}
              src={errorImage}
            />
          </Box>
        ) : null}
      </Box>
      <div style={{ marginTop: isTablet ? '0px' : '25%' }} />
    </>
  );
};

export { NotFound };
