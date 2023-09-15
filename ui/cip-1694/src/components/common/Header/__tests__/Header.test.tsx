/* eslint-disable no-var */
/* eslint-disable @typescript-eslint/no-explicit-any */
var mockUseCardano = jest.fn();
var mockToast = jest.fn();
var mockGetChainTip = jest.fn();
var mockSupportedWallets = ['Wallet1', 'Wallet2'];
import '@testing-library/jest-dom';
import React from 'react';
import { expect } from '@jest/globals';
import BlockIcon from '@mui/icons-material/Block';
import { screen, within, waitFor, fireEvent, cleanup } from '@testing-library/react';
import { createMemoryHistory } from 'history';
import { UserState } from 'common/store/types';
import { ROUTES } from 'common/routes';
import { renderWithProviders } from 'test/mockProviders';
import {
  chainTipMock,
  eventMock_active,
  eventMock_finished,
  eventMock_notStarted,
  useCardanoMock,
  useCardanoMock_notConnected,
} from 'test/mocks';
import { CustomRouter } from 'test/CustomRouter';
import { formatUTCDate } from 'pages/Leaderboard/utils';
import { getDateAndMonth } from 'common/utils/dateUtils';
import { Toast } from 'components/common/Toast/Toast';
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

jest.mock('common/api/voteService', () => ({
  ...jest.requireActual('common/api/voteService'),
  getChainTip: mockGetChainTip,
}));

jest.mock('react-hot-toast', () => ({
  __esModule: true,
  ...jest.requireActual('react-hot-toast'),
  default: mockToast,
}));

jest.mock('../../../../env', () => {
  const original = jest.requireActual('../../../../env');
  return {
    ...original,
    env: {
      ...original.env,
      SUPPORTED_WALLETS: mockSupportedWallets,
    },
  };
});

describe('For ongoing event:', () => {
  beforeEach(() => {
    mockUseCardano.mockReturnValue(useCardanoMock);
    mockGetChainTip.mockReturnValue(chainTipMock);
  });
  afterEach(() => {
    jest.clearAllMocks();
    cleanup();
  });

  test('should display proper state', async () => {
    const history = createMemoryHistory({ initialEntries: [ROUTES.INTRO] });
    renderWithProviders(
      <CustomRouter history={history}>
        <Header />
      </CustomRouter>,
      { preloadedState: { user: { event: eventMock_active, tip: chainTipMock } as UserState } }
    );

    await waitFor(async () => {
      const header = await screen.queryByTestId('header');
      expect(header).not.toBeNull();

      const headerLogo = await within(header).queryByTestId('header-logo');
      expect(headerLogo).not.toBeNull();
      expect(headerLogo.textContent).toEqual('CIP-1694 Ratification');

      const leaderboardLink = await within(header).queryByTestId('leaderboard-link');
      expect(leaderboardLink).not.toBeNull();
      expect(leaderboardLink.textContent).toEqual('Leaderboard');

      const voteLink = await within(header).queryByTestId('vote-link');
      expect(voteLink).not.toBeNull();
      expect(voteLink.textContent).toEqual('Your vote');

      expect(mockGetChainTip).toHaveBeenCalledTimes(1);
    });
  });

  test('should handle redirection between intro and vote pages', async () => {
    const history = createMemoryHistory({ initialEntries: [ROUTES.INTRO] });

    const historyPushSpy = jest.spyOn(history, 'push');
    renderWithProviders(
      <CustomRouter history={history}>
        <Header />
      </CustomRouter>,
      { preloadedState: { user: { event: eventMock_active, tip: chainTipMock } as UserState } }
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
      expect((historyPushSpy.mock.lastCall[0] as unknown as any).pathname).toEqual(ROUTES.INTRO);
    });
  });

  test('should render connect wallet button and open connect wallet modal once clicked', async () => {
    mockUseCardano.mockReset();
    mockUseCardano.mockReturnValue(useCardanoMock_notConnected);
    const history = createMemoryHistory({ initialEntries: [ROUTES.INTRO] });

    const { store } = renderWithProviders(
      <CustomRouter history={history}>
        <Header />
      </CustomRouter>,
      { preloadedState: { user: { event: eventMock_active, tip: chainTipMock } as UserState } }
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
    const history = createMemoryHistory({ initialEntries: [ROUTES.INTRO] });

    renderWithProviders(
      <CustomRouter history={history}>
        <Header />
      </CustomRouter>,
      { preloadedState: { user: { event: eventMock_active, tip: chainTipMock } as UserState } }
    );

    await waitFor(async () => {
      const header = await screen.queryByTestId('header');
      const connectWalletButton = await within(header).queryByTestId('connect-wallet-button');
      const connectedWalletButton = await within(header).queryByTestId('connected-wallet-button');
      expect(connectedWalletButton).not.toBeNull();
      expect(connectWalletButton).toBeNull();
    });
  });

  test('should show confirmation modal and handle redirection to leadeboard page', async () => {
    mockUseCardano.mockReset();
    mockUseCardano.mockReturnValue(useCardanoMock);

    const history = createMemoryHistory({ initialEntries: [ROUTES.INTRO] });

    const historyPushSpy = jest.spyOn(history, 'push');
    renderWithProviders(
      <CustomRouter history={history}>
        <Header />
      </CustomRouter>,
      { preloadedState: { user: { event: eventMock_active, tip: chainTipMock } as UserState } }
    );

    const header = await screen.findByTestId('header');

    const leaderboardLink = within(header).queryByTestId('leaderboard-link');
    expect(screen.queryByTestId('result-comming-soon-modal')).toBeNull();

    fireEvent.click(leaderboardLink);

    const confirmModal = screen.queryByTestId('result-comming-soon-modal');
    expect(confirmModal).not.toBeNull();

    fireEvent.click(within(confirmModal).queryByTestId('result-comming-soon-modal-cta'));
    await waitFor(() => {
      expect(screen.queryByTestId('result-comming-soon-modal')).toBeNull();
    });

    expect((historyPushSpy.mock.lastCall[0] as unknown as any).pathname).toEqual(ROUTES.LEADERBOARD);
  });

  test('should show confirmation modal and discard redirection to leadeboard page', async () => {
    mockUseCardano.mockReset();
    mockUseCardano.mockReturnValue(useCardanoMock);

    const history = createMemoryHistory({ initialEntries: [ROUTES.INTRO] });

    const historyPushSpy = jest.spyOn(history, 'push');
    renderWithProviders(
      <CustomRouter history={history}>
        <Header />
      </CustomRouter>,
      { preloadedState: { user: { event: eventMock_active, tip: chainTipMock } as UserState } }
    );

    const header = await screen.findByTestId('header');

    const leaderboardLink = within(header).queryByTestId('leaderboard-link');
    expect(screen.queryByTestId('result-comming-soon-modal')).toBeNull();

    fireEvent.click(leaderboardLink);

    const confirmModal = screen.queryByTestId('result-comming-soon-modal');
    expect(confirmModal).not.toBeNull();

    expect(within(confirmModal).queryByTestId('result-comming-soon-modal-close-cta')).toHaveTextContent('Go back');
    expect(within(confirmModal).queryByTestId('result-comming-soon-modal-cta')).toHaveTextContent(
      'View leaderboard anyway'
    );

    expect(within(confirmModal).queryByTestId('result-comming-soon-modal-description')).toHaveTextContent(
      `The results will be available from ${getDateAndMonth(
        eventMock_active?.proposalsRevealDate?.toString()
      )} ${formatUTCDate(eventMock_active?.proposalsRevealDate?.toString())}`
    );

    expect(within(confirmModal).queryByTestId('result-comming-soon-modal-title')).toHaveTextContent('Coming soon');
    fireEvent.click(within(confirmModal).queryByTestId('result-comming-soon-modal-close-cta'));

    await waitFor(() => {
      expect(screen.queryByTestId('result-comming-soon-modal')).toBeNull();
    });

    expect(historyPushSpy.mock.lastCall).toBeUndefined();
  });

  test('should show confirmation modal and discard redirection to leadeboard page on close icon click', async () => {
    mockUseCardano.mockReset();
    mockUseCardano.mockReturnValue(useCardanoMock);

    const history = createMemoryHistory({ initialEntries: [ROUTES.INTRO] });

    const historyPushSpy = jest.spyOn(history, 'push');
    renderWithProviders(
      <CustomRouter history={history}>
        <Header />
      </CustomRouter>,
      { preloadedState: { user: { event: eventMock_active, tip: chainTipMock } as UserState } }
    );

    const header = await screen.findByTestId('header');

    const leaderboardLink = within(header).queryByTestId('leaderboard-link');
    expect(screen.queryByTestId('result-comming-soon-modal')).toBeNull();

    fireEvent.click(leaderboardLink);

    const confirmModal = screen.queryByTestId('result-comming-soon-modal');
    expect(confirmModal).not.toBeNull();

    fireEvent.click(within(confirmModal).queryByTestId('result-comming-soon-modal-close-icon'));

    await waitFor(() => {
      expect(screen.queryByTestId('result-comming-soon-modal')).toBeNull();
    });

    expect(historyPushSpy.mock.lastCall).toBeUndefined();
  });

  test('should have leaderboard link disabled if there is no tip fetched', async () => {
    mockUseCardano.mockReset();
    mockUseCardano.mockReturnValue(useCardanoMock_notConnected);

    const history = createMemoryHistory({ initialEntries: [ROUTES.INTRO] });

    renderWithProviders(
      <CustomRouter history={history}>
        <Header />
      </CustomRouter>,
      { preloadedState: { user: { event: eventMock_active } as UserState } }
    );

    const header = await screen.findByTestId('header');

    const leaderboardLink = within(header).queryByTestId('leaderboard-link');
    expect(leaderboardLink.closest('button')).toHaveAttribute('disabled');
    expect(screen.queryByTestId('result-comming-soon-modal')).toBeNull();

    fireEvent.click(leaderboardLink);

    const confirmModal = screen.queryByTestId('result-comming-soon-modal');
    expect(confirmModal).toBeNull();
    expect(mockGetChainTip).not.toHaveBeenCalled();
  });

  test('should display toast if fetch chain tip request failed', async () => {
    mockGetChainTip.mockImplementation(async () => await Promise.reject('error'));

    const history = createMemoryHistory({ initialEntries: [ROUTES.INTRO] });
    renderWithProviders(
      <CustomRouter history={history}>
        <Header />
      </CustomRouter>,
      { preloadedState: { user: { event: eventMock_active } as UserState } }
    );

    await waitFor(async () => {
      expect(mockToast).toBeCalledWith(
        <Toast
          message="Failed to fecth chain tip"
          error
          icon={<BlockIcon style={{ fontSize: '19px', color: '#F5F9FF' }} />}
        />
      );
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
    const history = createMemoryHistory({ initialEntries: [ROUTES.INTRO] });
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
    const history = createMemoryHistory({ initialEntries: [ROUTES.INTRO] });
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

    const history = createMemoryHistory({ initialEntries: [ROUTES.INTRO] });

    const historyPushSpy = jest.spyOn(history, 'push');
    renderWithProviders(
      <CustomRouter history={history}>
        <Header />
      </CustomRouter>,
      {
        preloadedState: {
          user: {
            event: eventMock_finished,
            tip: { ...chainTipMock, epochNo: eventMock_finished.proposalsRevealEpoch },
          } as UserState,
        },
      }
    );

    await waitFor(async () => {
      const header = await screen.queryByTestId('header');

      const leaderboardLink = await within(header).queryByTestId('leaderboard-link');

      fireEvent.click(leaderboardLink);
      expect((historyPushSpy.mock.lastCall[0] as unknown as any).pathname).toEqual(ROUTES.LEADERBOARD);
    });
  });
});
