import React from 'react';
import { Slides } from 'components/common/Slides/Slides';
import { SlideItem } from 'components/common/Slides/Slides.types';
import './Introduction.scss';

export const introItems: SlideItem[] = [
  {
    image: '/static/cip-1694.jpg',
    title: 'A Vote on Minimum-Viable Governance',
    description:
      'Cardano has reached an incredible milestone. After six years of initial development and feature cultivation, the Cardano blockchain has reached the age of Voltaire. Guided by a principles-first approach and led by the community, this new age of Cardano advances inclusive accountability for all participants in the ecosystem. The time has come for a vote by the community on the way forward.',
  },
];

export const IntroductionPage = () => {
  return <Slides items={introItems} />;
};
