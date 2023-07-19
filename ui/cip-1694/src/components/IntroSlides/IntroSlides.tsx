import React, { useState } from 'react';
import { Link } from 'react-router-dom';
import { Swiper, SwiperSlide } from 'swiper/react';
import { Pagination, Navigation, Autoplay } from 'swiper';
import { Swiper as SwiperClass } from 'swiper/types';
import { styled } from '@mui/material/styles';
import CssBaseline from '@mui/material/CssBaseline';
import { Grid, Container, Typography, Button } from '@mui/material';
import { ROUTES } from 'common/routes';
import CountDownTimer from 'components/CountDownTimer/CountDownTimer';
import { SlideProps } from './IntroSlides.types';
import './IntroSlides.scss';
import 'swiper/css/navigation';

const HeroStyleImg = styled('img')(() => ({
  objectFit: 'cover',
  width: 550,
  height: 550,
  borderRadius: 16,
}));

const IntroSlides = ({ items }: SlideProps) => {
  const [swiper, setSwiper] = useState<SwiperClass | undefined>(undefined);
  const [activeIndex, setActiveIndex] = useState(0);

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
            <Container disableGutters>
              <Grid
                container
                direction={{ xs: 'column', sm: 'row' }}
                justifyContent={{ xs: 'center', sm: 'flex-start' }}
                alignItems="center"
                columnSpacing="46px"
              >
                <Grid
                  item
                  xs={6}
                  display="flex"
                  direction="column"
                  alignItems="flex-start"
                >
                  <Typography
                    variant="h2"
                    sx={{
                      mb: '12px',
                      fontSize: '56px',
                      color: '#061D3C',
                      textAlign: 'left !important',
                      fontWeight: 600,
                      letterSpacing: 'normal',
                    }}
                  >
                    {slide.title}
                  </Typography>
                  <Typography
                    component={'span'}
                    sx={{
                      mb: '24px',
                    }}
                  >
                    <CountDownTimer />
                  </Typography>
                  <Typography
                    variant="body1"
                    sx={{
                      color: '#39486C',
                      textAlign: 'left !important',
                      fontSize: '16px',
                      fontWeight: '400',
                      lineHeight: '22px',
                      mb: '40px',
                    }}
                  >
                    {slide.description}
                  </Typography>
                  <Button
                    size="large"
                    component={Link}
                    variant="contained"
                    to={{ pathname: ROUTES.VOTE }}
                    sx={{
                      color: '#F5F9FF !important',
                      fontFamily: 'Roboto',
                      fontSize: '16px',
                      fontStyle: 'normal',
                      fontWeight: '600',
                      lineHeight: 'normal',
                      borderRadius: '8px',
                      background: '#1D439B',
                      height: '49px',
                      width: '131px',
                      textTransform: 'unset',
                      whiteSpace: 'nowrap',
                    }}
                  >
                    Get started
                  </Button>
                </Grid>
                <Grid
                  item
                  xs={6}
                >
                  <HeroStyleImg
                    src={slide.image}
                    alt="cardano-summit-2022"
                  />
                </Grid>
              </Grid>
            </Container>
          </SwiperSlide>
        ))}
      </Swiper>
      {items.length > 1 && (
        <div className="pagination">
          {items.map((_, index) => (
            <div
              key={index}
              className={activeIndex === index ? 'page-indicator-active' : 'page-indicator'}
            />
          ))}
        </div>
      )}
    </div>
  );
};

export default IntroSlides;
