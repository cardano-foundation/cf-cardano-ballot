import React, { useState } from 'react';
import { Typography, Grid, Dialog } from '@mui/material';
import PlayCircleOutlineRoundedIcon from '@mui/icons-material/PlayCircleOutlineRounded';
import CARDANOSUMMIT2023LOGO from '../../common/resources/images/cardanosummit2023.svg';
import { Hexagon } from '../../components/common/Hexagon';
import './Home.scss';
import { i18n } from '../../i18n';
import { NavLink } from 'react-router-dom';
import { CustomButton } from '../../components/common/Button/CustomButton';

const Home: React.FC = () => {
  const [isModalOpen, setModalOpen] = useState(false);

  const handleOpenModal = () => {
    setModalOpen(true);
  };

  const handleCloseModal = () => {
    setModalOpen(false);
  };

  return (
    <Grid
      container
      spacing={1}
      sx={{
        height: { xs: '60%', md: '70%' },
        margin: { xs: '0%', sm: '2%', md: '3%', lg: '4%' },
      }}
    >
      <Grid
        item
        xs={12}
        md={7}
        sx={{
          flex: '1',
          padding: '20px',
          order: '1',
          display: 'flex'
        }}
      >
        <div className="left-title-container">
          <Typography
            className="title"
            variant="h2"
            sx={{ textAlign: { xs: 'center', sm: 'left' }, fontSize: { xs: '32px', sm: '48px', md: '56px' } }}
          >
            {i18n.t('landing.title')}
          </Typography>
          <Typography
            variant="body1"
            sx={{ textAlign: { xs: 'center', sm: 'left' } }}
          >
            {i18n.t('landing.description')}
          </Typography>
          <Grid
            container
            sx={{ justifyContent: { xs: 'center', sm: 'left' } }}
          >
            <NavLink
              to="/categories"
              style={{ textDecoration: 'none' }}
            >
              <CustomButton
                styles={{
                  background: '#ACFCC5',
                  color: '#03021F',
                  marginTop: '40px',
                  textDecoration: 'none !important',
                }}
                label={i18n.t('landing.getStartedButton')}
              />
            </NavLink>
          </Grid>
        </div>
      </Grid>

      <Grid
        item
        xs={12}
        md={5}
        justifyContent="center"
        alignItems="center"
        sx={{
          display: 'flex',
          order: '2',
        }}
      >
        <div className="hero-banner">
          <Hexagon>
            <>
              <img
                src={CARDANOSUMMIT2023LOGO}
                alt="CARDANO SUMMIT 2023"
                className="cardano-summit-logo"
              />
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
                  title="Cardano Summit 2023"
                  allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture"
                  allowFullScreen
                ></iframe>
              </Dialog>
            </>
          </Hexagon>
        </div>
      </Grid>
    </Grid>
  );
};

export { Home };
