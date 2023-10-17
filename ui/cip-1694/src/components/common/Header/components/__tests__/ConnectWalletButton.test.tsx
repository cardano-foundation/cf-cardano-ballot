/* eslint-disable no-var */
/* eslint-disable @typescript-eslint/no-explicit-any */
var mockUseCardano = jest.fn();
var mockToast = jest.fn();
var ConnectWalletButtonMock = jest.fn();
var getItemMock = jest.fn();
var setItemMock = jest.fn();
var removeItemMock = jest.fn();
var clearMock = jest.fn();
/* eslint-disable no-var */
import '@testing-library/jest-dom';
import React, { useEffect } from 'react';
import { expect } from '@jest/globals';
import BlockIcon from '@mui/icons-material/Block';
import { waitFor, cleanup } from '@testing-library/react';
import { createMemoryHistory } from 'history';
import { UserState } from 'common/store/types';
import { ROUTES } from 'common/routes';
import { renderWithProviders } from 'test/mockProviders';
import { eventMock_active, useCardanoMock } from 'test/mocks';
import { CustomRouter } from 'test/CustomRouter';
import { Toast } from 'components/common/Toast/Toast';
import { USER_SESSION_KEY } from 'common/utils/session';
import { ConnectWalletButton } from '../ConnectWalletButton';
import * as envFile from '../../../../../env';

const sessionStorageMock = (() => ({
  getItem: getItemMock,
  setItem: setItemMock,
  removeItem: removeItemMock,
  clear: clearMock,
}))();

Object.defineProperty(window, 'sessionStorage', {
  value: sessionStorageMock,
});

jest.mock('@cardano-foundation/cardano-connect-with-wallet', () => ({
  useCardano: mockUseCardano,
  NetworkType: {
    MAINNET: 'mainnet',
    TESTNET: 'testnet',
  },
  getWalletIcon: () => <span data-testid="getWalletIcon" />,
  ConnectWalletList: () => {
    return <span data-testid="connect-wallet-list" />;
  },
  ConnectWalletButton: ConnectWalletButtonMock,
}));

jest.mock('swiper/react', () => ({}));
jest.mock('swiper', () => ({}));

jest.mock('react-hot-toast', () => ({
  __esModule: true,
  ...jest.requireActual('react-hot-toast'),
  default: mockToast,
}));

describe('ConnectWalletButton', () => {
  beforeEach(() => {
    mockUseCardano.mockReturnValue(useCardanoMock);
  });
  afterEach(() => {
    jest.clearAllMocks();
    cleanup();
  });

  test('should display toast if no wallets provided', async () => {
    envFile.env.SUPPORTED_WALLETS = [];
    envFile.env.TARGET_NETWORK = 'Preprod';
    const history = createMemoryHistory({ initialEntries: [ROUTES.INTRO] });
    renderWithProviders(
      <CustomRouter history={history}>
        <ConnectWalletButton />
      </CustomRouter>,
      { preloadedState: { user: { event: eventMock_active } as UserState } }
    );

    await waitFor(async () => {
      expect(mockToast).toBeCalledWith(
        <Toast
          message="No supported wallets specified"
          error
          icon={<BlockIcon style={{ fontSize: '19px', color: '#F5F9FF' }} />}
        />
      );
    });
  });

  test('should handle onConnect', async () => {
    envFile.env.TARGET_NETWORK = 'Preprod';
    ConnectWalletButtonMock.mockReset();
    ConnectWalletButtonMock.mockImplementation(({ onConnect }: { onConnect: () => void }) => {
      useEffect(() => {
        onConnect();
      });
      return <span data-testid="connected-wallet-button" />;
    });
    const history = createMemoryHistory({ initialEntries: [ROUTES.INTRO] });
    const { store } = renderWithProviders(
      <CustomRouter history={history}>
        <ConnectWalletButton />
      </CustomRouter>,
      { preloadedState: { user: { event: eventMock_active } as UserState } }
    );

    expect(store.getState().user.isConnectWalletModalVisible).toBeFalsy;

    await waitFor(async () => {
      expect(store.getState().user.isConnectWalletModalVisible).toBeTruthy;
    });
  });

  test('should handle onDisconnect', async () => {
    envFile.env.TARGET_NETWORK = 'Preprod';
    const connectedWallet = 'connectedWallet';
    ConnectWalletButtonMock.mockReset();
    ConnectWalletButtonMock.mockImplementation(({ onDisconnect }: { onDisconnect: () => void }) => {
      useEffect(() => {
        onDisconnect();
      });
      return <span data-testid="connected-wallet-button" />;
    });
    const history = createMemoryHistory({ initialEntries: [ROUTES.INTRO] });
    const { store } = renderWithProviders(
      <CustomRouter history={history}>
        <ConnectWalletButton />
      </CustomRouter>,
      { preloadedState: { user: { event: eventMock_active, connectedWallet } as UserState } }
    );

    await waitFor(async () => {
      expect(store.getState().user.connectedWallet).toEqual('');
      expect(removeItemMock).toBeCalledWith(USER_SESSION_KEY);
      expect(clearMock).toBeCalled();
    });
  });
});
