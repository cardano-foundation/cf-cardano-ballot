/* eslint-disable no-var */
/* eslint-disable @typescript-eslint/no-explicit-any */
var mockUseCardano = jest.fn();
import '@testing-library/jest-dom';
import React from 'react';
import { expect } from '@jest/globals';
import { screen, within, waitFor, fireEvent, cleanup } from '@testing-library/react';
import { createMemoryHistory } from 'history';
import { UserState } from 'common/store/types';
import { ROUTES } from 'common/routes';
import { renderWithProviders } from '../../../../../test/mockProviders';
import {
  eventMock_active,
  eventMock_finished,
  eventMock_notStarted,
  useCardanoMock,
  useCardanoMock_notConnected,
} from '../../../../../test/mocks';
import { CustomRouter } from '../../../../../test/CustomRouter';
import { Header } from '../Header';

jest.mock('@cardano-foundation/cardano-connect-with-wallet', () => ({
  useCardano: mockUseCardano,
  getWalletIcon: () => <span data-testid="getWalletIcon" />,
  ConnectWalletList: () => {
    return <span data-testid="connect-wallet-list" />;
  },
  ConnectWalletButton: () => {
    return <span data-testid="connected-wallet-button" />;
  },
}));

jest.mock('swiper/react', () => ({}));
jest.mock('swiper', () => ({}));

describe('For ongoing event:', () => {
  beforeEach(() => {
    mockUseCardano.mockReturnValue(useCardanoMock);
  });
  afterEach(() => {
    jest.clearAllMocks();
    cleanup();
  });

  test('should display proper state', async () => {
    const history = createMemoryHistory({ initialEntries: ['/'] });
    renderWithProviders(
      <CustomRouter history={history}>
        <Header />
      </CustomRouter>,
      { preloadedState: { user: { event: eventMock_active } as UserState } }
    );

    await waitFor(async () => {
      const header = await screen.queryByTestId('header');
      expect(header).not.toBeNull();

      const headerLogo = await within(header).queryByTestId('header-logo');
      expect(headerLogo).not.toBeNull();
      expect(headerLogo.textContent).toEqual('CIP-1694 Ratification');

      const leaderboardLink = await within(header).queryByTestId('leaderboard-link');
      expect(leaderboardLink).toBeNull();

      const voteLink = await within(header).queryByTestId('vote-link');
      expect(voteLink).not.toBeNull();
      expect(voteLink.textContent).toEqual('Your vote');
    });
  });

  test('should handle redirection between intro and vote pages', async () => {
    const history = createMemoryHistory({ initialEntries: ['/'] });

    const historyPushSpy = jest.spyOn(history, 'push');
    renderWithProviders(
      <CustomRouter history={history}>
        <Header />
      </CustomRouter>,
      { preloadedState: { user: { event: eventMock_active } as UserState } }
    );

    await waitFor(async () => {
      const header = await screen.queryByTestId('header');
      const headerLogo = await within(header).queryByTestId('header-logo');
      expect(headerLogo.textContent).toEqual('CIP-1694 Ratification');

      const voteLink = await within(header).queryByTestId('vote-link');
      expect(voteLink.textContent).toEqual('Your vote');

      fireEvent.click(voteLink);
      expect((historyPushSpy.mock.lastCall[0] as unknown as any).pathname).toEqual(ROUTES.VOTE);

      fireEvent.click(headerLogo);
      expect((historyPushSpy.mock.lastCall[0] as unknown as any).pathname).toEqual('/');
    });
  });

  test('should render connect wallet button and open connect wallet modal once clicked', async () => {
    mockUseCardano.mockReset();
    mockUseCardano.mockReturnValue(useCardanoMock_notConnected);
    const history = createMemoryHistory({ initialEntries: ['/'] });

    const { store } = renderWithProviders(
      <CustomRouter history={history}>
        <Header />
      </CustomRouter>,
      { preloadedState: { user: { event: eventMock_active } as UserState } }
    );

    await waitFor(async () => {
      const header = await screen.queryByTestId('header');
      const connectWalletButton = await within(header).queryByTestId('connect-wallet-button');
      expect(connectWalletButton.textContent).toEqual('Connect wallet');

      expect(store.getState().user.isConnectWalletModalVisible).toBeFalsy;
      fireEvent.click(connectWalletButton);
      expect(store.getState().user.isConnectWalletModalVisible).toEqual(true);
    });
  });

  test('should render connected wallet button', async () => {
    mockUseCardano.mockReset();
    mockUseCardano.mockReturnValue(useCardanoMock);
    const history = createMemoryHistory({ initialEntries: ['/'] });

    renderWithProviders(
      <CustomRouter history={history}>
        <Header />
      </CustomRouter>,
      { preloadedState: { user: { event: eventMock_active } as UserState } }
    );

    await waitFor(async () => {
      const header = await screen.queryByTestId('header');
      const connectWalletButton = await within(header).queryByTestId('connect-wallet-button');
      const connectedWalletButton = await within(header).queryByTestId('connected-wallet-button');
      expect(connectedWalletButton).not.toBeNull();
      expect(connectWalletButton).toBeNull();
    });
  });
});

describe("For the event that hasn't started yet", () => {
  beforeEach(() => {
    mockUseCardano.mockReturnValue(useCardanoMock_notConnected);
  });
  afterEach(() => {
    jest.clearAllMocks();
    cleanup();
  });

  test('should display proper state', async () => {
    const history = createMemoryHistory({ initialEntries: ['/'] });
    renderWithProviders(
      <CustomRouter history={history}>
        <Header />
      </CustomRouter>,
      { preloadedState: { user: { event: eventMock_notStarted } as UserState } }
    );

    await waitFor(async () => {
      const header = await screen.queryByTestId('header');
      expect(header).not.toBeNull();

      const headerLogo = await within(header).queryByTestId('header-logo');
      expect(headerLogo).not.toBeNull();

      const leaderboardLink = await within(header).queryByTestId('leaderboard-link');
      expect(leaderboardLink).toBeNull();

      const voteLink = await within(header).queryByTestId('vote-link');
      expect(voteLink).toBeNull();

      const connectWalletButton = await within(header).queryByTestId('connect-wallet-button');
      expect(connectWalletButton).not.toBeNull();
    });
  });
});

describe('For the event that has already finished', () => {
  beforeEach(() => {
    mockUseCardano.mockReturnValue(useCardanoMock_notConnected);
  });
  afterEach(() => {
    jest.clearAllMocks();
    cleanup();
  });

  test('should display proper state', async () => {
    const history = createMemoryHistory({ initialEntries: ['/'] });
    renderWithProviders(
      <CustomRouter history={history}>
        <Header />
      </CustomRouter>,
      { preloadedState: { user: { event: eventMock_finished } as UserState } }
    );

    await waitFor(async () => {
      const header = await screen.queryByTestId('header');
      expect(header).not.toBeNull();

      const headerLogo = await within(header).queryByTestId('header-logo');
      expect(headerLogo).not.toBeNull();

      const leaderboardLink = await within(header).queryByTestId('leaderboard-link');
      expect(leaderboardLink).not.toBeNull();
      expect(leaderboardLink.textContent).toEqual('Leaderboard');

      const voteLink = await within(header).queryByTestId('vote-link');
      expect(voteLink).not.toBeNull();

      const connectWalletButton = await within(header).queryByTestId('connect-wallet-button');
      expect(connectWalletButton).not.toBeNull();
    });
  });

  test('should handle redirection to leadeboard page', async () => {
    mockUseCardano.mockReset();
    mockUseCardano.mockReturnValue(useCardanoMock);

    const history = createMemoryHistory({ initialEntries: ['/'] });

    const historyPushSpy = jest.spyOn(history, 'push');
    renderWithProviders(
      <CustomRouter history={history}>
        <Header />
      </CustomRouter>,
      { preloadedState: { user: { event: eventMock_finished } as UserState } }
    );

    await waitFor(async () => {
      const header = await screen.queryByTestId('header');

      const leaderboardLink = await within(header).queryByTestId('leaderboard-link');

      fireEvent.click(leaderboardLink);
      expect((historyPushSpy.mock.lastCall[0] as unknown as any).pathname).toEqual(ROUTES.LEADERBOARD);
    });
  });
});
