/* eslint-disable @typescript-eslint/no-explicit-any */
/* eslint-disable no-var */
var mockUseCardano = jest.fn();
/* eslint-disable import/imports-first */
import React from 'react';
import '@testing-library/jest-dom';
import { expect } from '@jest/globals';
import { screen, within, waitFor, fireEvent, cleanup, act } from '@testing-library/react';
import { createMemoryHistory } from 'history';
import { ROUTES } from 'common/routes';
import { UserState } from 'common/store/types';
import { IntroductionPage } from 'pages/Introduction/Introduction';
import { renderWithProviders } from 'test/mockProviders';
import { eventMock_active, useCardanoMock, eventMock_notStarted, eventMock_finished } from 'test/mocks';
import { CustomRouter } from 'test/CustomRouter';
import { formatUTCDate } from 'common/utils/dateUtils';

const title = 'A Vote on Minimum-Viable Governance';
const description =
  'Cardano has reached an incredible milestone. After six years of initial development and feature cultivation, the Cardano blockchain has reached the age of Voltaire. Guided by a principles-first approach and led by the community, this new age of Cardano advances inclusive accountability for all participants in the ecosystem. The time has come for a vote by the community on the way forward.';
const imageSrc = '/static/cip-1694.jpg';

jest.mock('../../../env', () => {
  const original = jest.requireActual('../../../env');
  return {
    ...original,
    env: {
      ...original.env,
      TARGET_NETWORK: 'Preprod',
    },
  };
});

jest.mock('@cardano-foundation/cardano-connect-with-wallet', () => {
  return {
    useCardano: mockUseCardano,
    NetworkType: {
      MAINNET: 'mainnet',
      TESTNET: 'testnet',
    },
    getWalletIcon: () => <span data-testid="getWalletIcon" />,
    ConnectWalletList: () => {
      return <span data-testid="ConnectWalletList" />;
    },
    ConnectWalletButton: () => {
      return <span data-testid="ConnectWalletButton" />;
    },
  };
});

describe('For ongoing event:', () => {
  beforeEach(() => {
    mockUseCardano.mockReturnValue(useCardanoMock);
  });
  afterEach(() => {
    jest.clearAllMocks();
    cleanup();
  });

  test('should display proper state when the event related data is loading', async () => {
    const history = createMemoryHistory({ initialEntries: [ROUTES.INTRO] });

    renderWithProviders(
      <CustomRouter history={history}>
        <IntroductionPage />
      </CustomRouter>
    );

    await waitFor(async () => {
      const introductionPage = screen.queryByTestId('introduction-page');

      const eventTime = within(introductionPage).queryByTestId('event-time');
      expect(eventTime).not.toBeNull();
      expect(eventTime.textContent).toEqual('Voting closes: ');

      const preloader = within(introductionPage).queryByTestId('event-time-loader');
      expect(preloader).not.toBeNull();
    });
  });

  test('should display proper state', async () => {
    const history = createMemoryHistory({ initialEntries: [ROUTES.INTRO] });

    renderWithProviders(
      <CustomRouter history={history}>
        <IntroductionPage />
      </CustomRouter>,
      { preloadedState: { user: { event: eventMock_active } as UserState } }
    );

    await waitFor(async () => {
      const introductionPage = screen.queryByTestId('introduction-page');
      expect(introductionPage).not.toBeNull();

      const eventTitle = within(introductionPage).queryByTestId('event-title');
      expect(eventTitle).not.toBeNull();
      expect(eventTitle.textContent).toEqual(title);

      const eventTime = within(introductionPage).queryByTestId('event-time');
      expect(eventTime).not.toBeNull();
      expect(eventTime.textContent).toEqual(
        `Voting closes: ${formatUTCDate(eventMock_active.eventEndDate.toString())}`
      );

      const eventDescription = within(introductionPage).queryByTestId('event-description');
      expect(eventDescription).not.toBeNull();
      expect(eventDescription.textContent).toEqual(description);

      const cta = within(introductionPage).queryByTestId('event-cta');
      expect(cta).not.toBeNull();
      expect(cta.textContent).toEqual('Get started');

      const image = within(introductionPage).queryByTestId('event-image');
      expect(image).not.toBeNull();
      expect(image.tagName).toEqual('IMG');
      expect(image.attributes.getNamedItem('src').value).toEqual(imageSrc);
    });
  });

  test('should redirect to vote page', async () => {
    const history = createMemoryHistory({ initialEntries: [ROUTES.INTRO] });
    const historyPushSpy = jest.spyOn(history, 'push');

    renderWithProviders(
      <CustomRouter history={history}>
        <IntroductionPage />
      </CustomRouter>,
      { preloadedState: { user: { event: eventMock_active } as UserState } }
    );

    await waitFor(async () => {
      const cta = within(screen.queryByTestId('introduction-page')).queryByTestId('event-cta');

      fireEvent.click(cta);
      expect((historyPushSpy.mock.lastCall[0] as unknown as any).pathname).toEqual(ROUTES.VOTE);
    });
    historyPushSpy.mockRestore();
  });
});

describe("For the event that hasn't started yet", () => {
  beforeEach(() => {
    mockUseCardano.mockReturnValue(useCardanoMock);
  });
  afterEach(() => {
    jest.clearAllMocks();
    cleanup();
  });

  test('should display proper state', async () => {
    mockUseCardano.mockReset();
    mockUseCardano.mockReturnValue({
      useCardanoMock,
      isConnected: false,
    });

    const history = createMemoryHistory({ initialEntries: [ROUTES.INTRO] });

    renderWithProviders(
      <CustomRouter history={history}>
        <IntroductionPage />
      </CustomRouter>,
      { preloadedState: { user: { event: eventMock_notStarted } as UserState } }
    );

    await waitFor(async () => {
      const introductionPage = screen.queryByTestId('introduction-page');
      expect(introductionPage).not.toBeNull();

      const eventTitle = within(introductionPage).queryByTestId('event-title');
      expect(eventTitle).not.toBeNull();
      expect(eventTitle.textContent).toEqual(title);

      const eventTime = within(introductionPage).queryByTestId('event-time');
      expect(eventTime).not.toBeNull();
      expect(eventTime.textContent).toEqual(
        `Vote from: ${formatUTCDate(eventMock_active.eventStartDate.toString())} - ${formatUTCDate(
          eventMock_active.eventEndDate.toString()
        )}`
      );

      const eventDescription = within(introductionPage).queryByTestId('event-description');
      expect(eventDescription).not.toBeNull();
      expect(eventDescription.textContent).toEqual(description);

      const cta = within(introductionPage).queryByTestId('event-cta');
      expect(cta).not.toBeNull();
      expect(cta.textContent).toEqual('Get started');

      const image = within(introductionPage).queryByTestId('event-image');
      expect(image).not.toBeNull();
      expect(image.tagName).toEqual('IMG');
      expect(image.attributes.getNamedItem('src').value).toEqual(imageSrc);
    });
  });

  test('should display connect wallet modal', async () => {
    mockUseCardano.mockReset();
    mockUseCardano.mockReturnValue({
      useCardanoMock,
      isConnected: false,
    });
    const history = createMemoryHistory({ initialEntries: [ROUTES.INTRO] });
    const historyPushSpy = jest.spyOn(history, 'push');

    const { store } = renderWithProviders(
      <CustomRouter history={history}>
        <IntroductionPage />
      </CustomRouter>,
      { preloadedState: { user: { event: eventMock_notStarted } as UserState } }
    );

    const cta = within(screen.queryByTestId('introduction-page')).queryByTestId('event-cta');

    await act(async () => {
      fireEvent.click(cta);
    });

    await waitFor(async () => {
      expect(store.getState().user.isConnectWalletModalVisible).toBeTruthy();
    });
    historyPushSpy.mockRestore();
  });

  test('should redirect to vote page', async () => {
    const history = createMemoryHistory({ initialEntries: [ROUTES.INTRO] });
    const historyPushSpy = jest.spyOn(history, 'push');

    renderWithProviders(
      <CustomRouter history={history}>
        <IntroductionPage />
      </CustomRouter>,
      { preloadedState: { user: { event: eventMock_notStarted } as UserState } }
    );

    const introductionPage = screen.queryByTestId('introduction-page');
    expect(within(introductionPage).queryByTestId('event-cta').textContent).toEqual('Preview the question');

    await waitFor(async () => {
      const cta = within(screen.queryByTestId('introduction-page')).queryByTestId('event-cta');

      fireEvent.click(cta);
      expect((historyPushSpy.mock.lastCall[0] as unknown as any).pathname).toEqual(ROUTES.VOTE);
    });
    historyPushSpy.mockRestore();
  });
});

describe('For the event that has already finished', () => {
  beforeEach(() => {
    mockUseCardano.mockReturnValue(useCardanoMock);
  });
  afterEach(() => {
    jest.clearAllMocks();
    cleanup();
  });

  test('should display proper state', async () => {
    const history = createMemoryHistory({ initialEntries: [ROUTES.INTRO] });

    renderWithProviders(
      <CustomRouter history={history}>
        <IntroductionPage />
      </CustomRouter>,
      { preloadedState: { user: { event: eventMock_finished } as UserState } }
    );

    await waitFor(async () => {
      const introductionPage = screen.queryByTestId('introduction-page');
      expect(introductionPage).not.toBeNull();

      const eventTitle = within(introductionPage).queryByTestId('event-title');
      expect(eventTitle).not.toBeNull();
      expect(eventTitle.textContent).toEqual(title);

      const eventTime = within(introductionPage).queryByTestId('event-time');
      expect(eventTime).not.toBeNull();
      expect(eventTime.textContent).toEqual(
        `The vote closed on ${formatUTCDate(eventMock_active.eventEndDate.toString())}`
      );

      const eventDescription = within(introductionPage).queryByTestId('event-description');
      expect(eventDescription).not.toBeNull();
      expect(eventDescription.textContent).toEqual(description);

      const cta = within(introductionPage).queryByTestId('event-cta');
      expect(cta).not.toBeNull();
      expect(cta.textContent).toEqual('See the results');

      const image = within(introductionPage).queryByTestId('event-image');
      expect(image).not.toBeNull();
      expect(image.tagName).toEqual('IMG');
      expect(image.attributes.getNamedItem('src').value).toEqual(imageSrc);
    });
  });

  test('should redirect to vote page', async () => {
    const history = createMemoryHistory({ initialEntries: [ROUTES.INTRO] });
    const historyPushSpy = jest.spyOn(history, 'push');

    renderWithProviders(
      <CustomRouter history={history}>
        <IntroductionPage />
      </CustomRouter>,
      { preloadedState: { user: { event: eventMock_finished } as UserState } }
    );

    await waitFor(async () => {
      const cta = within(screen.queryByTestId('introduction-page')).queryByTestId('event-cta');

      fireEvent.click(cta);
      expect((historyPushSpy.mock.lastCall[0] as unknown as any).pathname).toEqual(ROUTES.LEADERBOARD);
    });
    historyPushSpy.mockRestore();
  });
});
