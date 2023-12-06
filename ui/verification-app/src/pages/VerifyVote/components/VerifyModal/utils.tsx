import React from 'react';
import { ERRORS, SECTIONS } from './types';

export const titles = {
  [SECTIONS.VERIFY]: 'Verify your ballot',
  [SECTIONS.CHOSE_EXPLORER]: 'Viewing transaction details',
};

export const descriptions = {
  [SECTIONS.VERIFY]: (
    <>
      To authenticate your ballot, please paste your Ballot Proof into the text field below (the Ballot Proof can be
      found on your ballot receipt, on the top of the Cardano Ballot site under <b>Your Ballot</b>). After this, click
      on the "Verify" button to complete the verification process
    </>
  ),
  [SECTIONS.CHOSE_EXPLORER]:
    'Where would you like to see your transaction details displayed after your verification has been completed?',
};

export const errors = {
  [ERRORS.VERIFY]: 'Unable to verify ballot',
  [ERRORS.JSON]: 'Invalid JSON. Please try again',
  [ERRORS.UNSUPPORTED_EVENT]: 'Unsupported event',
};

export const ctas = {
  [SECTIONS.VERIFY]: 'Verify',
  [SECTIONS.CHOSE_EXPLORER]: 'Confirm',
};
