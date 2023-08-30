import React, { useState } from 'react';
import { Button, Typography, Grid, useTheme, useMediaQuery, Dialog, Box } from '@mui/material';
import PlayCircleOutlineRoundedIcon from '@mui/icons-material/PlayCircleOutlineRounded';
import { Hexagon } from '../common/Hexagon';
import './Hero.scss';
import { i18n } from '../../i18n';
import { NavLink } from 'react-router-dom';

const Hero: React.FC = () => {
  const [isModalOpen, setModalOpen] = useState(false);
  const theme = useTheme();
  const isMobile = useMediaQuery(theme.breakpoints.down('sm'));

  const handleOpenModal = () => {
    setModalOpen(true);
  };

  const handleCloseModal = () => {
    setModalOpen(false);
  };

  return (
    <Box
      sx={{
        display: 'flex',
        flexDirection: { xs: 'column', sm: 'row' },
        height: { xs: 'auto', sm: '400px' },
        marginLeft: isMobile ? '0px' : '150px',
        marginTop: isMobile ? '0px' : '50px',
      }}
    >
      <Box
        sx={{
          flex: '1',
          padding: '20px',
          marginRight: isMobile ? '0px' : '40px',
          order: { xs: '1', sm: '1' },
        }}
      >
        <div
          className="left-title-container"
          style={{ marginTop: isMobile ? '0px' : '15%' }}
        >
          <Typography
            className="title"
            variant="h2"
            style={{ textAlign: isMobile ? 'center' : 'left' }}
          >
            {i18n.t('landing.title')}
          </Typography>
          <Typography
            variant="body1"
            style={{ textAlign: isMobile ? 'center' : 'left', marginTop: isMobile ? '40px' : '40px' }}
          >
            {i18n.t('landing.description')}
          </Typography>
          <Grid
            container
            justifyContent={isMobile ? 'center' : 'flex-start'}
          >
            <NavLink to="/categories">
              <Button className="get-started-button">{i18n.t('landing.getStartedButton')}</Button>
            </NavLink>
          </Grid>
        </div>
      </Box>

      <Box
        sx={{
          flex: '1',
          padding: '20px',
          marginLeft: isMobile ? '0px' : '40px',
          order: { xs: '2', sm: '2' },
        }}
      >
        <div style={{ paddingRight: isMobile ? '0px' : '20%' }}>
          <Hexagon>
            <>
              <div className="right-title-container">
                {i18n
                  .t('landing.hexagon.title')
                  .trim()
                  .split(' ')
                  .map((titleChunk, key) => (
                    <span key={key}>{titleChunk}</span>
                  ))}
              </div>
              <PlayCircleOutlineRoundedIcon
                className="play-icon"
                onClick={handleOpenModal}
              />
              <Dialog
                open={isModalOpen}
                onClose={handleCloseModal}
                maxWidth="md"
                fullWidth
              >
                <iframe
                  width="100%"
                  height="600px"
                  src="https://www.youtube.com/embed/UiY5-ycvM7w"
                  title="Video de YouTube"
                  allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture"
                  allowFullScreen
                ></iframe>
              </Dialog>
            </>
          </Hexagon>
        </div>
      </Box>
    </Box>
  );
};

export { Hero };
