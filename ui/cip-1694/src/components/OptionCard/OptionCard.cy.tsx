/// <reference types="cypress" />

import React from 'react';
import DoneIcon from '@mui/icons-material/Done';
import CloseIcon from '@mui/icons-material/Close';
import DoDisturbIcon from '@mui/icons-material/DoDisturb';
import { OptionCard } from './OptionCard';

const items = [
  {
    label: 'Yes',
    icon: <DoneIcon />,
  },
  {
    label: 'No',
    icon: <CloseIcon />,
  },
  {
    label: 'Abstain',
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
        items={items}
        onChangeOption={onChangeOption}
      />
    );
  });
});
