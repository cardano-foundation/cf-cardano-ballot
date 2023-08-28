import React from 'react';
import './Hexagon.scss';

const Hexagon = (props: { children: React.ReactNode }) => {
  return (
    <div className="hexagon-wrapper">
      <div className="second-hexagon"></div>
      <div className="third-hexagon"></div>
      <div className="fourth-hexagon"></div>
      <div className="fifth-hexagon"></div>
      <div className="hexagon-container">
        <div className="hexagon-content">{props.children}</div>
      </div>
    </div>
  );
};

export { Hexagon };
