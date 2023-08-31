import React from 'react';
import '@testing-library/jest-dom';
import { expect } from '@jest/globals';
import { screen, within, waitFor, cleanup, render } from '@testing-library/react';
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
  });

  test('should display proper t&c', async () => {
    render(<Footer />);

    await waitFor(async () => {
      const footer = await screen.queryByTestId('footer');
      expect(footer).not.toBeNull();

      const tandc = await within(footer).queryByTestId('t-and-c');
      expect(tandc).not.toBeNull();
      expect(tandc.textContent).toEqual('Terms & Conditions');
      expect(tandc.attributes.getNamedItem('href').value).toEqual('pdf');
      expect(tandc.attributes.getNamedItem('type').value).toEqual('application/pdf');
    });
  });

  test('should display proper privacy policy', async () => {
    render(<Footer />);

    await waitFor(async () => {
      const footer = await screen.queryByTestId('footer');
      expect(footer).not.toBeNull();

      const privacy = await within(footer).queryByTestId('privacy');
      expect(privacy).not.toBeNull();
      expect(privacy.textContent).toEqual('Privacy');
      expect(privacy.attributes.getNamedItem('href').value).toEqual('pdf');
      expect(privacy.attributes.getNamedItem('type').value).toEqual('application/pdf');
    });
  });

  // add tests for status
  // add tests for discord
});
