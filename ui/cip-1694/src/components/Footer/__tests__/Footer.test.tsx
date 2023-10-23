const discordMock = 'DISCORD_URL';
const faqMock = 'FAQ_URL';
import React from 'react';
import '@testing-library/jest-dom';
import { expect } from '@jest/globals';
import { screen, within, waitFor, cleanup, render } from '@testing-library/react';
import { Footer } from '../Footer';
import * as env from '../../../env';

const oldEnv = env.env;

describe('Footer', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    cleanup();
  });

  afterEach(() => {
    Object.defineProperty(env, 'env', {
      value: oldEnv,
    });
  });

  test('should display proper copyright', async () => {
    render(<Footer />);

    await waitFor(async () => {
      const footer = screen.queryByTestId('footer');
      expect(footer).not.toBeNull();

      const copyright = within(footer).queryByTestId('copyright');
      expect(copyright).not.toBeNull();
      expect(copyright.textContent).toEqual(
        `© ${new Date().getFullYear()} CIP-1694 Ratification. All rights reserved.`
      );
    });
  });

  test('should display proper FAQ', async () => {
    Object.defineProperty(env, 'env', {
      value: { FAQ_URL: faqMock },
    });

    render(<Footer />);

    await waitFor(async () => {
      const footer = screen.queryByTestId('footer');
      expect(footer).not.toBeNull();

      const faq = within(footer).queryByTestId('f-a-q');
      expect(faq).not.toBeNull();
      expect(faq.textContent).toEqual('FAQ');
      expect(faq.attributes.getNamedItem('href').value).toEqual(faqMock);
    });
  });

  test('should not display proper FAQ', async () => {
    render(<Footer />);

    await waitFor(async () => {
      const footer = screen.queryByTestId('footer');
      expect(footer).not.toBeNull();

      const faq = within(footer).queryByTestId('f-a-q');
      expect(faq).toBeNull();
    });
  });

  test('should display proper privacy policy', async () => {
    render(<Footer />);

    await waitFor(async () => {
      const footer = screen.queryByTestId('footer');
      expect(footer).not.toBeNull();

      const privacy = within(footer).queryByTestId('privacy');
      expect(privacy).not.toBeNull();
      expect(privacy.textContent).toEqual('Privacy');
      expect(privacy.attributes.getNamedItem('href').value).toEqual('pdf');
      expect(privacy.attributes.getNamedItem('type').value).toEqual('application/pdf');
    });
  });

  test('should display proper t&c', async () => {
    render(<Footer />);

    await waitFor(async () => {
      const footer = screen.queryByTestId('footer');
      expect(footer).not.toBeNull();

      const tandc = within(footer).queryByTestId('t-and-c');
      expect(tandc).not.toBeNull();
      expect(tandc.textContent).toEqual('Terms & Conditions');
      expect(tandc.attributes.getNamedItem('href').value).toEqual('pdf');
      expect(tandc.attributes.getNamedItem('type').value).toEqual('application/pdf');
    });
  });

  test('should display proper privacy policy', async () => {
    render(<Footer />);

    await waitFor(async () => {
      const footer = screen.queryByTestId('footer');
      expect(footer).not.toBeNull();

      const privacy = within(footer).queryByTestId('privacy');
      expect(privacy).not.toBeNull();
      expect(privacy.textContent).toEqual('Privacy');
      expect(privacy.attributes.getNamedItem('href').value).toEqual('pdf');
      expect(privacy.attributes.getNamedItem('type').value).toEqual('application/pdf');
    });
  });

  test('should display proper discord icon', async () => {
    Object.defineProperty(env, 'env', {
      value: { DISCORD_URL: discordMock },
    });
    render(<Footer />);

    await waitFor(async () => {
      const footer = screen.queryByTestId('footer');
      expect(footer).not.toBeNull();

      const discord = within(footer).queryByTestId('discord');
      expect(discord).not.toBeNull();
      expect(discord.children.length).toEqual(1);
      expect(discord.attributes.getNamedItem('href').value).toEqual(discordMock);
      expect(discord.attributes.getNamedItem('target').value).toEqual('_blank');
    });
  });
});
