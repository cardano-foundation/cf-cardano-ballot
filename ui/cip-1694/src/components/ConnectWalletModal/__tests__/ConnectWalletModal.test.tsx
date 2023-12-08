/* eslint-disable no-var */
var mockConnectWalletList = jest.fn();
var mockSupportedWallets = ['Wallet1', 'Wallet2', 'Typhoncip30'];
import React from 'react';
import '@testing-library/jest-dom';
import { expect } from '@jest/globals';
import { screen, within, waitFor, cleanup, render, fireEvent } from '@testing-library/react';
import { ConnectWalletModal } from '../ConnectWalletModal';
import { connectWalletListCustomCss } from '../utils';

jest.mock('../../../env', () => {
  const original = jest.requireActual('../../../env');
  return {
    ...original,
    env: {
      ...original.env,
      CATEGORY_ID: 'CHANGE_GOV_STRUCTURE',
      EVENT_ID: 'CIP-1694_Pre_Ratification_3316',
      SUPPORTED_WALLETS: mockSupportedWallets,
      TARGET_NETWORK: 'Preprod',
    },
  };
});

jest.mock('@cardano-foundation/cardano-connect-with-wallet', () => ({
  useCardano: jest.fn(),
  NetworkType: {
    MAINNET: 'mainnet',
    TESTNET: 'testnet',
  },
  getWalletIcon: () => <span data-testid="getWalletIcon" />,
  ConnectWalletList: mockConnectWalletList,
  ConnectWalletButton: () => {
    return <span data-testid="connected-wallet-button" />;
  },
}));

describe('ConnectWalletModal', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    cleanup();
  });

  const props = {
    name: 'connect-wallet-modal',
    id: 'connect-wallet-modal',
    title: 'Connect wallet',
    description: `In order to participate, first you will need to connect your wallet. Following wallets are accepted: ${mockSupportedWallets
      ?.map((w) => {
        const walletName = w.replace('typhoncip30', 'Typhon');
        return `${walletName[0].toUpperCase()}${walletName.slice(1)}`;
      })
      ?.join(', ')}.`,
    onConnectWallet: jest.fn(),
    onConnectWalletError: jest.fn(),
    onCloseFn: jest.fn(),
    installedExtensions: ['Wallet2'],
  };

  test('should display proper state', async () => {
    mockConnectWalletList.mockImplementation(() => {
      return <span data-testid="connect-wallet-list" />;
    });

    render(
      <ConnectWalletModal
        {...props}
        openStatus
      />
    );

    const modal = await screen.findByTestId('connected-wallet-modal');
    expect(modal).not.toBeNull();

    expect(within(modal).queryByTestId('connected-wallet-modal-title')).toHaveTextContent(props.title);
    expect(within(modal).queryByTestId('connected-wallet-modal-description')).toHaveTextContent(props.description);

    fireEvent.click(within(modal).queryByTestId('connected-wallet-modal-close'));
    await waitFor(() => {
      expect(props.onCloseFn).toBeCalledTimes(1);
    });

    expect(within(modal).queryByTestId('connect-wallet-list')).not.toBeNull();
    expect(mockConnectWalletList.mock.lastCall[0]).toEqual({
      showUnavailableWallets: 0,
      supportedWallets: props.installedExtensions,
      onConnect: props.onConnectWallet,
      onConnectError: props.onConnectWalletError,
      customCSS: connectWalletListCustomCss,
    });
  });

  test('should not render', async () => {
    mockConnectWalletList.mockImplementation(() => {
      return <span data-testid="connect-wallet-list" />;
    });

    render(
      <ConnectWalletModal
        {...props}
        openStatus={false}
      />
    );

    await waitFor(() => {
      const modal = screen.queryByTestId('connected-wallet-modal');
      expect(modal).toBeNull();
    });
  });

  test('should render proper name for typhon wallet', async () => {
    mockConnectWalletList.mockImplementation(({ supportedWallets }: { supportedWallets: string[] }) => {
      return (
        <span data-testid="connect-wallet-list">
          {supportedWallets.map((extension) => (
            <span key={extension}>{extension}</span>
          ))}
        </span>
      );
    });

    render(
      <ConnectWalletModal
        {...props}
        installedExtensions={['Typhoncip30']}
        openStatus
      />
    );

    await waitFor(() => {
      const listItems = Array.from(screen.queryByTestId('connect-wallet-list').children);
      expect(listItems.length).toEqual(1);
      expect(listItems[0].textContent).toEqual('Typhon');
    });
  });

  test('should render installed extensions in alphabetical order', async () => {
    mockConnectWalletList.mockImplementation(({ supportedWallets }: { supportedWallets: string[] }) => {
      return (
        <span data-testid="connect-wallet-list">
          {supportedWallets.map((extension) => (
            <span key={extension}>{extension}</span>
          ))}
        </span>
      );
    });

    render(
      <ConnectWalletModal
        {...props}
        installedExtensions={['Wallet2', 'Wallet1']}
        openStatus
      />
    );

    await waitFor(() => {
      const listItems = Array.from(screen.queryByTestId('connect-wallet-list').children);
      expect(listItems.length).toEqual(2);
      expect(listItems[0].textContent).toEqual('Wallet1');
      expect(listItems[1].textContent).toEqual('Wallet2');
    });
  });
});
