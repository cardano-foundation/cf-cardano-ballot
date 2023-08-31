import React from 'react';
import { Box, useTheme, useMediaQuery, Typography, Button } from '@mui/material';
import { useNavigate } from 'react-router-dom';
import { ROUTES } from '../../routes';
import './NotFound.scss';
import errorImage from '../../common/resources/images/404-error.svg';

const NotFound = () => {
  const navigate = useNavigate();
  const theme = useTheme();
  const isMobile = useMediaQuery(theme.breakpoints.down('sm'));
  const isLarger = useMediaQuery(theme.breakpoints.up('xxl'));

  return (
    <>
      <Box
        sx={{
          display: 'flex',
          flexDirection: { xs: 'column', sm: 'row' },
          height: { xs: 'auto', sm: '400px' },
        }}
      >
        <Box
          sx={{
            flex: '1',
            padding: '20px',
            marginRight: isMobile ? '0px' : '40px',
            marginTop: !isMobile ? '10%' : '0px',
            order: { xs: '1', sm: '1' },
          }}
        >
          <Typography
            className="nominees-title"
            variant="h4"
          >
            Page Not Found
          </Typography>
          <Typography
            className="nominees-description"
            style={{ width: isMobile ? '320px' : '550px', wordBreak: 'break-word' }}
            variant="body1"
            gutterBottom
          >
            Sorry, but it seems the page you're searching for doesn’t exist. Please feel free to click the button below
            and return to the home page, or you can use the navigation located at the top of the page to find your way
            around.
          </Typography>
            <Box sx={{ display: 'flex', justifyContent: isMobile ? 'center' : 'start', marginTop: isMobile ? '60px' : '40px' }}>
                <Button
                    className="go-home-button"
                    style={{ width: 'auto' }}
                    onClick={() => navigate(ROUTES.INTRO)}
                >
                    Go home
                </Button>
            </Box>
        </Box>

          {
              !isMobile ? <Box
                  sx={{
                      flex: '1',
                      padding: '20px',
                      order: { xs: '2', sm: '2' },
                  }}
              >
                  <img
                      style={{ width: isMobile ? '300px' : 'auto' }}
                      src={errorImage}
                  />
              </Box> : null
          }

      </Box>
      <div style={{ marginTop: isMobile ? '0px' : isLarger ? '25%' : '15%' }} />
    </>
  );
};

export { NotFound };
