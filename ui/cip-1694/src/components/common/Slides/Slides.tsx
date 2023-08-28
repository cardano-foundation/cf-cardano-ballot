import React, { useState } from 'react';
import { useSelector } from 'react-redux';
import { Link } from 'react-router-dom';
import cn from 'classnames';
import { Swiper, SwiperSlide } from 'swiper/react';
import { Pagination, Navigation, Autoplay } from 'swiper';
import { Swiper as SwiperClass } from 'swiper/types';
import CssBaseline from '@mui/material/CssBaseline';
import { Grid, Container, Typography, Button, Box } from '@mui/material';
import { RootState } from 'common/store';
import { ROUTES } from 'common/routes';
import { EventTime } from 'components/EventTime/EventTime';
import { SlideProps } from './Slides.types';
import styles from './Slides.module.scss';

export const Slides = ({ items }: SlideProps) => {
  const event = useSelector((state: RootState) => state.user.event);
  const [swiper, setSwiper] = useState<SwiperClass | undefined>(undefined);
  const [activeIndex, setActiveIndex] = useState(0);

  return (
    <Box
      data-testid="introduction-page"
      margin={{
        xs: '0px',
        md: '43px 0px',
      }}
      className={styles.slides}
    >
      <Swiper
        className={cn('swiper-container', styles.swiperContainer)}
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
          <SwiperSlide
            className={styles.sliderWrapper}
            key={index}
          >
            <CssBaseline />
            <Container disableGutters>
              <Grid
                marginTop={{ xs: '5px', md: '0px' }}
                container
                direction={{ xs: 'column-reverse', md: 'row' }}
                justifyContent={{ xs: 'center', md: 'flex-start' }}
                alignItems={{ xs: 'flex-start', md: 'center' }}
                columnSpacing={{ md: '46px' }}
                gap={{ xs: '25px', md: '0px' }}
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
                    data-testid="event-title"
                    fontSize={{
                      xs: '28px',
                      md: '56px',
                    }}
                    lineHeight={{
                      xs: '33px',
                      md: '65px',
                    }}
                  >
                    {slide.title}
                  </Typography>
                  <Typography
                    component={'span'}
                    sx={{
                      mb: '24px',
                    }}
                    fontSize={{
                      xs: '16px',
                      md: '18px',
                    }}
                  >
                    <EventTime
                      eventHasntStarted={event?.notStarted}
                      eventHasFinished={event?.finished}
                      endTime={event?.eventEnd?.toString()}
                      startTime={event?.eventStart?.toString()}
                    />
                  </Typography>
                  <Typography
                    variant="body1"
                    className={styles.description}
                    data-testid="event-description"
                    marginBottom={{
                      xs: '25px',
                      md: '40px',
                    }}
                  >
                    {slide.description}
                  </Typography>
                  <Button
                    size="large"
                    component={Link}
                    variant="contained"
                    className={styles.button}
                    data-testid="event-cta"
                    to={{ pathname: ROUTES[event?.finished ? 'LEADERBOARD' : 'VOTE'] }}
                  >
                    {event?.notStarted ? 'View the vote' : event?.finished ? 'See the results' : 'Get started'}
                  </Button>
                </Grid>
                <Grid
                  item
                  xs={6}
                  height="auto"
                  width={{ xs: '100%', md: '550px' }}
                >
                  <img
                    className={styles.heroStyleImg}
                    data-testid="event-image"
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
    </Box>
  );
};
