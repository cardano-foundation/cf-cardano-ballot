/// <reference types="cypress" />

import React from 'react';
import { EventTime } from './EventTime';

describe('<EventTime />', () => {
  it('renders', () => {
    // see: https://on.cypress.io/mounting-react
    cy.mount(
      <EventTime
        endTime={new Date()}
        startTime={new Date()}
      />
    );
  });
});
