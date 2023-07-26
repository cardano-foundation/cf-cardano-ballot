/// <reference types="cypress" />

import React from 'react';
import DoneIcon from '@mui/icons-material/Done';
import CloseIcon from '@mui/icons-material/Close';
import DoDisturbIcon from '@mui/icons-material/DoDisturb';
import { OptionCard } from './OptionCard';
import { OptionItem } from './OptionCard.types';

const items: OptionItem[] = [
  {
    label: 'yes',
    icon: <DoneIcon />,
  },
  {
    label: 'no',
    icon: <CloseIcon />,
  },
  {
    label: 'abstain',
    icon: <DoDisturbIcon />,
  },
];

const onChangeOption = (option: string) => {
  return option;
};

describe('<OptionCard />', () => {
  it('renders', () => {
    cy.fixture('items.json').as('items');
    cy.mount(
      <OptionCard
        selectedOption="Yes"
        items={items}
        onChangeOption={onChangeOption}
      />
    );
  });
});
