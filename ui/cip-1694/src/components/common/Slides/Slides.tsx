import 'swiper/css/navigation';
import React, { useState } from 'react';
import { Link } from 'react-router-dom';
import { Swiper, SwiperSlide } from 'swiper/react';
import { Pagination, Navigation, Autoplay } from 'swiper';
import { Swiper as SwiperClass } from 'swiper/types';
import CssBaseline from '@mui/material/CssBaseline';
import { Grid, Container, Typography, Button } from '@mui/material';
import { ROUTES } from 'common/routes';
import CountDownTimer from 'components/CountDownTimer/CountDownTimer';
import { SlideProps } from './Slides.types';
import styles from './Slides.module.scss';

export const Slides = ({ items }: SlideProps) => {
  const [swiper, setSwiper] = useState<SwiperClass | undefined>(undefined);
  const [activeIndex, setActiveIndex] = useState(0);

  return (
    <div className={styles.slides}>
      <Swiper
        className="swiper-container"
        onSwiper={setSwiper}
        onSlideChange={() => (swiper ? setActiveIndex(swiper.realIndex) : undefined)}
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
                  container
                  item
                  xs={6}
                  display="flex"
                  direction="column"
                  alignItems="flex-start"
                >
                  <Typography
                    variant="h2"
                    className={styles.title}
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
                    className={styles.description}
                  >
                    {slide.description}
                  </Typography>
                  <Button
                    size="large"
                    component={Link}
                    variant="contained"
                    className={styles.button}
                    to={{ pathname: ROUTES.VOTE }}
                  >
                    Get started
                  </Button>
                </Grid>
                <Grid
                  item
                  xs={6}
                >
                  <img
                    className={styles.heroStyleImg}
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
