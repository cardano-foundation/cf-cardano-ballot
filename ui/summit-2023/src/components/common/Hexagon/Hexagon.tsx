import React from 'react';
import './Hexagon.scss';
import { useMediaQuery, useTheme } from '@mui/material';

const Hexagon = (props: { children: React.ReactNode }) => {
  const theme = useTheme();
  const isMobile = useMediaQuery(theme.breakpoints.down('sm'));
  return (
    <div className="hexagon-wrapper">
      <div className="second-hexagon"></div>
      <div className="third-hexagon"></div>
      <div className="fourth-hexagon"></div>
      <div className="fifth-hexagon"></div>
      <div className="hexagon-container">
        <div
          style={{ fontSize: isMobile ? '70%' : '90%' }}
          className="hexagon-content"
        >
          {props.children}
        </div>
      </div>
    </div>
  );
};

export { Hexagon };
