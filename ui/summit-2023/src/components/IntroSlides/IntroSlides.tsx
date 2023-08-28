import React, { useState } from 'react';
import { Swiper, SwiperSlide } from 'swiper/react';
import { Pagination, Navigation, Autoplay } from 'swiper';
import { Swiper as SwiperClass } from 'swiper/types';
import { styled, useTheme } from '@mui/material/styles';
import CssBaseline from '@mui/material/CssBaseline';
import { Grid, Container, Typography, Button } from '@mui/material';
import { Link } from 'react-router-dom';
import { SlideProps } from './IntroSlides.types';
import './IntroSlides.scss';
import 'swiper/css/navigation';

// TODO: let's deside on the approach we use? either classes or inline ones?
const ContentStyle = styled('div')(({ theme }) => ({
  display: 'flex',
  justifyContent: 'flex-left',
  flexDirection: 'column',
  padding: theme.spacing(2, 0),
  [theme.breakpoints.up('md')]: {
    alignItems: 'flex-start',
    padding: theme.spacing(5, 0, 0, 5),
  },
}));

const QuestionStyle = styled('div')(({ theme }) => ({
  maxWidth: 530,
  display: 'flex',
  flexDirection: 'column',
  justifyContent: 'flex-left',
  margin: theme.spacing(2, 0, 2, 2),
}));

const HeroStyleImg = styled('img')(({ theme }) => ({
  top: 0,
  width: 600,
  height: 700,
  objectFit: 'cover',
  borderRadius: 16,
  [theme.breakpoints.up('md')]: {
    maxWidth: 450,
  },
}));

// TODO: could we rename this to be more a generic one? just "slides"?
const IntroSlides = ({ items }: SlideProps) => {
  const [swiper, setSwiper] = useState<SwiperClass | undefined>(undefined);
  const [activeIndex, setActiveIndex] = useState(0);
  const theme = useTheme();

  return (
    <div className="slides">
      <Swiper
        className="swiper-container"
        onSwiper={setSwiper}
        onSlideChange={() => (swiper ? setActiveIndex(swiper.realIndex) : null)}
        slidesPerView={1}
        navigation={false}
        autoplay={{
          delay: 3000,
          disableOnInteraction: false,
        }}
        loop={true}
        modules={[Autoplay, Pagination, Navigation]}
      >
        {items.map((slide, index) => (
          <SwiperSlide key={index}>
            <CssBaseline />
            <Container>
              <Grid
                container
                direction={{ xs: 'column', sm: 'row' }}
                justifyContent={{ xs: 'center', sm: 'flex-start' }}
                alignItems="center"
                spacing={5}
              >
                <Grid item>
                  <QuestionStyle>
                    <Typography
                      variant="h2"
                      sx={{
                        color: 'text.primary',
                        textAlign: 'left',
                        fontWeight: 600,
                      }}
                    >
                      {slide.title}
                    </Typography>
                    <Typography
                      variant="body1"
                      sx={{ color: 'text.secondary', textAlign: 'left' }}
                    >
                      {slide.description}
                    </Typography>
                  </QuestionStyle>
                  <Button
                    size="large"
                    component={Link}
                    variant="contained"
                    // TODO: move to routes const?
                    to={{ pathname: '/vote' }}
                    sx={{
                      marginTop: '0px !important',
                      height: { xs: '50px', sm: '60px', lg: '70px' },
                      fontSize: '25px',
                      fontWeight: 700,
                      textTransform: 'none',
                      borderRadius: '16px !important',
                      color: '#fff !important',
                      fontFamily: 'Roboto Bold',
                      backgroundColor: theme.palette.primary.main,
                    }}
                  >
                    Ready to submit vote?
                  </Button>
                </Grid>
                <Grid item>
                  <ContentStyle>
                    <HeroStyleImg
                      src={slide.image}
                      alt="cardano-summit-2022"
                    />
                  </ContentStyle>
                </Grid>
              </Grid>
            </Container>
          </SwiperSlide>
        ))}
      </Swiper>
      <div className="pagination">
        {items.map((_, index) => (
          <div
            key={index}
            className={activeIndex === index ? 'page-indicator-active' : 'page-indicator'}
          />
        ))}
      </div>
    </div>
  );
};

export default IntroSlides;
