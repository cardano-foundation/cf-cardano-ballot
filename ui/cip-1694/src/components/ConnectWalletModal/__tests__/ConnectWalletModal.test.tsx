/* eslint-disable no-var */
var mockConnectWalletList = jest.fn();
var mockSupportedWallets = ['Wallet1', 'Wallet2'];
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
    },
  };
});

jest.mock('@cardano-foundation/cardano-connect-with-wallet', () => ({
  useCardano: jest.fn(),
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
    description: 'In order to vote, first you will need to connect your wallet.',
    onConnectWallet: jest.fn(),
    onConnectWalletError: jest.fn(),
    onCloseFn: jest.fn(),
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
      supportedWallets: mockSupportedWallets,
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
});
