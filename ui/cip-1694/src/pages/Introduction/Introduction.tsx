import React from 'react';
import { Slides } from 'components/common/Slides/Slides';
import { SlideItem } from 'components/common/Slides/Slides.types';
import './Introduction.scss';

export const introItems: SlideItem[] = [
  {
    image: '/static/cip-1694-community-workshop.jpeg',
    title: 'What is CIP-1694 voting?',
    description:
      'Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magnaaliqua. Sit amet justo donec enim diam vulputate.',
  },
];

export const IntroductionPage = () => {
  return <Slides items={introItems} />;
};
