/* eslint-disable @typescript-eslint/no-explicit-any */
/* eslint-disable no-var */
var mockUseCardano = jest.fn();
/* eslint-disable import/imports-first */
import React from 'react';
import '@testing-library/jest-dom';
import { expect } from '@jest/globals';
import { screen, within, waitFor, fireEvent, cleanup } from '@testing-library/react';
import { createMemoryHistory } from 'history';
import { ROUTES } from 'common/routes';
import { UserState } from 'common/store/types';
import { IntroductionPage, introItems } from 'pages/Introduction/Introduction';
import { renderWithProviders } from 'test/mockProviders';
import { eventMock_active, useCardanoMock, eventMock_notStarted, eventMock_finished } from 'test/mocks';
import { CustomRouter } from 'test/CustomRouter';
import { formatUTCDate } from 'common/utils/dateUtils';

jest.mock('@cardano-foundation/cardano-connect-with-wallet', () => {
  return {
    useCardano: mockUseCardano,
    getWalletIcon: () => <span data-testid="getWalletIcon" />,
    ConnectWalletList: () => {
      return <span data-testid="ConnectWalletList" />;
    },
    ConnectWalletButton: () => {
      return <span data-testid="ConnectWalletButton" />;
    },
  };
});

jest.mock('swiper/react', () => ({
  Swiper: ({ children }: { children: React.ReactElement }) => <div data-testid="Swiper-testId">{children}</div>,
  SwiperSlide: ({ children }: { children: React.ReactElement }) => (
    <div data-testid="SwiperSlide-testId">{children}</div>
  ),
}));

jest.mock('swiper', () => ({
  Pagination: () => null,
  Navigation: () => null,
  Autoplay: () => null,
}));

describe('For ongoing event:', () => {
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
      { preloadedState: { user: { event: eventMock_active } as UserState } }
    );

    await waitFor(async () => {
      const introductionPage = await screen.queryByTestId('introduction-page');
      expect(introductionPage).not.toBeNull();

      const eventTitle = await within(introductionPage).queryByTestId('event-title');
      expect(eventTitle).not.toBeNull();
      expect(eventTitle.textContent).toEqual(introItems[0].title);

      const eventTime = await within(introductionPage).queryByTestId('event-time');
      expect(eventTime).not.toBeNull();
      expect(eventTime.textContent).toEqual(`Voting closes: ${formatUTCDate(eventMock_active.eventEnd.toString())}`);

      const eventDescription = await within(introductionPage).queryByTestId('event-description');
      expect(eventDescription).not.toBeNull();
      expect(eventDescription.textContent).toEqual(introItems[0].description);

      const cta = await within(introductionPage).queryByTestId('event-cta');
      expect(cta).not.toBeNull();
      expect(cta.textContent).toEqual('Get started');

      const image = await within(introductionPage).queryByTestId('event-image');
      expect(image).not.toBeNull();
      expect(image.tagName).toEqual('IMG');
      expect(image.attributes.getNamedItem('src').value).toEqual(introItems[0].image);
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
      const cta = await within(await screen.queryByTestId('introduction-page')).queryByTestId('event-cta');

      fireEvent.click(cta);
      expect((historyPushSpy.mock.lastCall[0] as unknown as any).pathname).toEqual(ROUTES.VOTE);
    });
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
    const history = createMemoryHistory({ initialEntries: [ROUTES.INTRO] });

    renderWithProviders(
      <CustomRouter history={history}>
        <IntroductionPage />
      </CustomRouter>,
      { preloadedState: { user: { event: eventMock_notStarted } as UserState } }
    );

    await waitFor(async () => {
      const introductionPage = await screen.queryByTestId('introduction-page');
      expect(introductionPage).not.toBeNull();

      const eventTitle = await within(introductionPage).queryByTestId('event-title');
      expect(eventTitle).not.toBeNull();
      expect(eventTitle.textContent).toEqual(introItems[0].title);

      const eventTime = await within(introductionPage).queryByTestId('event-time');
      expect(eventTime).not.toBeNull();
      expect(eventTime.textContent).toEqual(
        `Vote from: ${formatUTCDate(eventMock_active.eventStart.toString())} - ${formatUTCDate(
          eventMock_active.eventEnd.toString()
        )}`
      );

      const eventDescription = await within(introductionPage).queryByTestId('event-description');
      expect(eventDescription).not.toBeNull();
      expect(eventDescription.textContent).toEqual(introItems[0].description);

      const cta = await within(introductionPage).queryByTestId('event-cta');
      expect(cta).not.toBeNull();
      expect(cta.textContent).toEqual('View the vote');

      const image = await within(introductionPage).queryByTestId('event-image');
      expect(image).not.toBeNull();
      expect(image.tagName).toEqual('IMG');
      expect(image.attributes.getNamedItem('src').value).toEqual(introItems[0].image);
    });
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

    await waitFor(async () => {
      const cta = await within(await screen.queryByTestId('introduction-page')).queryByTestId('event-cta');

      fireEvent.click(cta);
      expect((historyPushSpy.mock.lastCall[0] as unknown as any).pathname).toEqual(ROUTES.VOTE);
    });
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
      const introductionPage = await screen.queryByTestId('introduction-page');
      expect(introductionPage).not.toBeNull();

      const eventTitle = await within(introductionPage).queryByTestId('event-title');
      expect(eventTitle).not.toBeNull();
      expect(eventTitle.textContent).toEqual(introItems[0].title);

      const eventTime = await within(introductionPage).queryByTestId('event-time');
      expect(eventTime).not.toBeNull();
      expect(eventTime.textContent).toEqual(
        `The vote closed on ${formatUTCDate(eventMock_active.eventEnd.toString())}`
      );

      const eventDescription = await within(introductionPage).queryByTestId('event-description');
      expect(eventDescription).not.toBeNull();
      expect(eventDescription.textContent).toEqual(introItems[0].description);

      const cta = await within(introductionPage).queryByTestId('event-cta');
      expect(cta).not.toBeNull();
      expect(cta.textContent).toEqual('See the results');

      const image = await within(introductionPage).queryByTestId('event-image');
      expect(image).not.toBeNull();
      expect(image.tagName).toEqual('IMG');
      expect(image.attributes.getNamedItem('src').value).toEqual(introItems[0].image);
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
      const cta = await within(await screen.queryByTestId('introduction-page')).queryByTestId('event-cta');

      fireEvent.click(cta);
      expect((historyPushSpy.mock.lastCall[0] as unknown as any).pathname).toEqual(ROUTES.LEADERBOARD);
    });
  });
});
