
import React from 'react';
import '@testing-library/jest-dom';
import { expect } from '@jest/globals';
import { screen, within, waitFor, cleanup,render } from '@testing-library/react';
import { Footer } from '../Footer';

describe('Footer', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    cleanup();
  });

  test('should display proper copyright', async () => {
    render(<Footer />);

    await waitFor(async () => {
      const footer = await screen.queryByTestId('footer');
      expect(footer).not.toBeNull();

      const copyright = await within(footer).queryByTestId('copyright');
      expect(copyright).not.toBeNull();
      expect(copyright.textContent).toEqual(
        `© ${new Date().getFullYear()} CIP-1694 Ratification. All rights reserved.`
      );
    });

    // add tests for t-and-c
    // add tests for privacy
    // add tests for status
    // add tests for discord
  });
});
