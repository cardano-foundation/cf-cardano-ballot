/// <reference types="cypress" />

import React from 'react';
import OptionCard from './OptionCard';
import DoneIcon from "@mui/icons-material/Done";
import CloseIcon from "@mui/icons-material/Close";
import DoDisturbIcon from "@mui/icons-material/DoDisturb";

const items = [
  {
    label: "Yes",
    icon: <DoneIcon />,
  },
  {
    label: "No",
    icon: <CloseIcon />,
  },
  {
    label: "Abstain",
    icon: <DoDisturbIcon />,
  },
];

describe('<OptionCard />', () => {
  it('renders', () => {
    cy.fixture('items.json').as('items');
    cy.mount(<OptionCard items={items}/>);
  })
})