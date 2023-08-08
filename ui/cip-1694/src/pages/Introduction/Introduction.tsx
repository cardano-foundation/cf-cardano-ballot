import React from 'react';
import { useSelector } from 'react-redux';
import { RootState } from 'common/store';
import { Slides } from '../../components/common/Slides/Slides';
import { SlideItem } from '../../components/common/Slides/Slides.types';
import './Introduction.scss';

const Introduction = () => {
  const event = useSelector((state: RootState) => state.user.event);

  const items: SlideItem[] = [
    {
      image: '/static/cip-1694-community-workshop.jpeg',
      title: event?.presentationName,
      description:
        'Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magnaaliqua. Sit amet justo donec enim diam vulputate.',
    },
  ];

  return <Slides items={items} />;
};

export default Introduction;
