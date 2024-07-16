/* eslint-disable no-var */
var mockConnectWalletList = jest.fn();
var mockToast = jest.fn();
var getItemMock = jest.fn();
var setItemMock = jest.fn();
var removeItemMock = jest.fn();
var clearMock = jest.fn();
var mockSupportedWallets = ['Wallet1', 'Wallet2'];
import React from 'react';
import '@testing-library/jest-dom';
import { expect } from '@jest/globals';
import { screen, within, cleanup, act, fireEvent } from '@testing-library/react';
import { createMemoryHistory } from 'history';
import BlockIcon from '@mui/icons-material/Block';
import { UserState } from 'common/store/types';
import { ROUTES } from 'common/routes';
import { Toast } from 'components/Toast/Toast';
import { USER_SESSION_KEY } from 'common/utils/session';
import { useCardanoMock } from 'test/mocks';
import { Content } from '../Content';
import { renderWithProviders } from '../../../test/mockProviders';
import { CustomRouter } from '../../../test/CustomRouter';

const sessionStorageMock = (() => ({
  getItem: getItemMock,
  setItem: setItemMock,
  removeItem: removeItemMock,
  clear: clearMock,
}))();

Object.defineProperty(window, 'sessionStorage', {
  value: sessionStorageMock,
});

jest.mock('react-hot-toast', () => mockToast);

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
  useCardano: jest.fn(() => ({
    installedExtensions: useCardanoMock.installedExtensions,
  })),
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

jest.mock('@mui/material', () => ({
  ...jest.requireActual('@mui/material'),
  debounce: jest.fn((fn) => fn),
}));

describe('ConnectWalletModal', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    cleanup();
  });

  test('should display proper state for connect wallet modal, properly react to wallet on connect error scenario', async () => {
    mockConnectWalletList.mockImplementation(() => {
      return <span data-testid="connect-wallet-list" />;
    });

    const history = createMemoryHistory({ initialEntries: [ROUTES.INTRO] });
    const { store } = renderWithProviders(
      <CustomRouter history={history}>
        <Content />
      </CustomRouter>,
      { preloadedState: { user: { isConnectWalletModalVisible: true } as UserState } }
    );

    expect(store.getState().user.isConnectWalletModalVisible).toBeTruthy();
    const modal = await screen.findByTestId('connected-wallet-modal');
    expect(modal).not.toBeNull();

    expect(within(modal).queryByTestId('connected-wallet-modal-title')).toHaveTextContent('Connect wallet');
    expect(within(modal).queryByTestId('connected-wallet-modal-description')).toHaveTextContent(
      `In order to participate, first you will need to connect your wallet. Following wallets are accepted: ${mockSupportedWallets
        ?.map((w) => {
          const walletName = w.replace('typhoncip30', 'Typhon');
          return `${walletName[0].toUpperCase()}${walletName.slice(1)}`;
        })
        ?.join(', ')}.`
    );

    expect(within(modal).queryByTestId('connect-wallet-list')).not.toBeNull();
    await act(async () => {
      mockConnectWalletList.mock.lastCall[0].onConnectError();
    });

    expect(mockToast).toBeCalledWith(
      <Toast
        error={true}
        icon={<BlockIcon style={{ color: '#F5F9FF', fontSize: '19px' }} />}
        message="Unable to connect your wallet. Please try again"
      />
    );

    await act(async () => {
      fireEvent.click(within(modal).queryByTestId('connected-wallet-modal-close'));
    });
    expect(store.getState().user.isConnectWalletModalVisible).toBeFalsy();
  });

  test('should properly react to wallet on connect scenario', async () => {
    mockConnectWalletList.mockImplementation(() => {
      return <span data-testid="connect-wallet-list" />;
    });

    const history = createMemoryHistory({ initialEntries: [ROUTES.INTRO] });
    const { store } = renderWithProviders(
      <CustomRouter history={history}>
        <Content />
      </CustomRouter>,
      { preloadedState: { user: { isConnectWalletModalVisible: true } as UserState } }
    );

    await act(async () => {
      mockConnectWalletList.mock.lastCall[0].onConnect();
    });

    expect(mockToast).toBeCalledWith(<Toast message="Wallet Connected!" />);
    expect(store.getState().user.isConnectWalletModalVisible).toBeFalsy();
    expect(removeItemMock).toBeCalledWith(USER_SESSION_KEY);
    expect(clearMock).toBeCalled();
  });
});
