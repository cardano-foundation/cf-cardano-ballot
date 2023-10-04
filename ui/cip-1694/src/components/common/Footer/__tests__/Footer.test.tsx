const discordMock = 'DISCORD_URL';
import React from 'react';
import '@testing-library/jest-dom';
import { expect } from '@jest/globals';
import { screen, within, waitFor, cleanup, render } from '@testing-library/react';
import { Footer } from '../Footer';

jest.mock('../../../../env', () => {
  const original = jest.requireActual('../../../../env');
  return {
    ...original,
    env: {
      ...original.env,
      DISCORD_URL: discordMock,
    },
  };
});

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

  test('should display proper discord icon', async () => {
    render(<Footer />);

    await waitFor(async () => {
      const footer = await screen.queryByTestId('footer');
      expect(footer).not.toBeNull();

      const discord = await within(footer).queryByTestId('discord');
      expect(discord).not.toBeNull();
      expect(discord.children.length).toEqual(1);
      expect(discord.attributes.getNamedItem('href').value).toEqual(discordMock);
      expect(discord.attributes.getNamedItem('target').value).toEqual('_blank');
    });
  });
});
