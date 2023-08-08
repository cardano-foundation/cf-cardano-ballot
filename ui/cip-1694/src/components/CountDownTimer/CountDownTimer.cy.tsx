/// <reference types="cypress" />

import React from 'react';
import CountDownTimer from './CountDownTimer';

describe('<CountDownTimer />', () => {
  it('renders', () => {
    // see: https://on.cypress.io/mounting-react
    cy.mount(<CountDownTimer endTime={new Date()} />);
  });
});
