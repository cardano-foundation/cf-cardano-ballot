import React from 'react';
import { Hero } from '../../components/Hero';
import { useMediaQuery, useTheme } from '@mui/material';

const Home = () => {
  const theme = useTheme();
  const isMobile = useMediaQuery(theme.breakpoints.down('sm'));
  const isLarger = useMediaQuery(theme.breakpoints.up('xxl'));

  return (
    <>
      <Hero />
      <div style={{ marginTop: isMobile ? '0px' : isLarger ? '25%' : '15%' }} />
    </>
  );
};

export { Home };
