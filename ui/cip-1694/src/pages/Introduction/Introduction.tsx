import React from 'react';
import IntroSlides from '../../components/IntroSlides/IntroSlides';
import { SlideItem } from '../../components/IntroSlides/IntroSlides.types';
import './Introduction.scss';

const Introduction = () => {
  const items: SlideItem[] = [
    {
      image: '/static/cip-1694-community-workshop.jpeg',
      title: 'What is CIP-1694 voting?',
      description:
        'Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magnaaliqua. Sit amet justo donec enim diam vulputate.',
    },
    {
      image: '/static/brazil-cip-1694.jpeg',
      title: 'What is CIP-1694 voting?',
      description:
        'Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magnaaliqua. Sit amet justo donec enim diam vulputate.',
    },
    {
      image: '/static/ci-1694-tokyo.jpeg',
      title: 'What is CIP-1694 voting?',
      description:
        'Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magnaaliqua. Sit amet justo donec enim diam vulputate.',
    },
  ];

  return <IntroSlides items={items} />;
};

export default Introduction;
