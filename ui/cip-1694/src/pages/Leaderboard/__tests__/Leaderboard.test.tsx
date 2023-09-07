/* eslint-disable @typescript-eslint/no-explicit-any */
/* eslint-disable no-var */
var mockUseCardano = jest.fn();
var mockGetStats = jest.fn();
var mockPieChart = jest.fn();
/* eslint-disable import/imports-first */
import 'whatwg-fetch';
import '@testing-library/jest-dom';
import { expect } from '@jest/globals';
import { screen, within, waitFor, cleanup } from '@testing-library/react';
import React from 'react';
import { createMemoryHistory } from 'history';
import capitalize from 'lodash/capitalize';
import { ROUTES } from 'common/routes';
import { UserState } from 'common/store/types';
import { renderWithProviders } from 'test/mockProviders';
import { useCardanoMock, eventMock_finished, voteStats, eventMock_active } from 'test/mocks';
import { CustomRouter } from 'test/CustomRouter';
import { ByCategory } from 'types/voting-app-types';
import { Leaderboard } from '../Leaderboard';
import { proposalColorsMap, getPercentage } from '../utils';

jest.mock('react-minimal-pie-chart', () => ({
  PieChart: mockPieChart,
}));

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

jest.mock('../../../env', () => {
  const original = jest.requireActual('../../../env');
  return {
    ...original,
    env: {
      ...original.env,
      CATEGORY_ID: 'CIP-1694_Pre_Ratification_4619',
      EVENT_ID: 'CIP-1694_Pre_Ratification_4619',
    },
  };
});

jest.mock('common/api/leaderboardService', () => ({
  ...jest.requireActual('common/api/leaderboardService'),
  getStats: mockGetStats,
}));

describe('For the event that has already finished', () => {
  beforeEach(() => {
    mockUseCardano.mockReturnValue(useCardanoMock);
    mockGetStats.mockReturnValue(voteStats);
  });
  afterEach(() => {
    jest.clearAllMocks();
    cleanup();
  });

  test('should display proper state', async () => {
    mockPieChart.mockImplementation(() => <div data-testid="pie-chart" />);
    const history = createMemoryHistory({ initialEntries: [ROUTES.LEADERBOARD] });

    renderWithProviders(
      <CustomRouter history={history}>
        <Leaderboard />
      </CustomRouter>,
      { preloadedState: { user: { event: eventMock_finished } as UserState } }
    );

    const stats = Object.entries(voteStats.proposals);
    const statsSum = Object.values(voteStats.proposals)?.reduce((acc, { votes }) => (acc += votes), 0);
    const statsItems =
      eventMock_finished?.categories
        ?.find(({ id }) => id === 'CIP-1694_Pre_Ratification_4619')
        ?.proposals?.map(({ name }) => ({
          name,
          label: capitalize(name.toLowerCase()),
        })) || [];

    await waitFor(async () => {
      const leaderboardPage = await screen.queryByTestId('leaderboard-page');
      expect(leaderboardPage).not.toBeNull();

      const leaderboardTitle = await within(leaderboardPage).queryByTestId('leaderboard-title');
      expect(leaderboardTitle).not.toBeNull();
      expect(leaderboardTitle.textContent).toEqual('Leaderboard');

      const pollStatsTile = await within(leaderboardPage).queryByTestId('poll-stats-tile');
      expect(pollStatsTile).not.toBeNull();

      const pollStatsTileTitle = await within(pollStatsTile).queryByTestId('tile-title');
      expect(pollStatsTileTitle).not.toBeNull();
      expect(pollStatsTileTitle.textContent).toEqual('Poll stats');

      const pollStatsTileSummary = await within(pollStatsTile).queryByTestId('tile-summary');
      expect(pollStatsTileSummary).not.toBeNull();
      expect(pollStatsTileSummary.textContent).toEqual(`${statsSum}`);

      const pollStatsItems = await within(pollStatsTile).queryAllByTestId('poll-stats-item');
      expect(pollStatsItems[0].textContent).toEqual(`${capitalize(stats[0][0].toLowerCase())}${stats[0][1].votes}`);
      expect(pollStatsItems[1].textContent).toEqual(`${capitalize(stats[1][0].toLowerCase())}${stats[1][1].votes}`);
      expect(pollStatsItems[2].textContent).toEqual(`${capitalize(stats[2][0].toLowerCase())}${stats[2][1].votes}`);

      const currentlyVotingTile = await within(leaderboardPage).queryByTestId('currently-voting-tile');
      expect(currentlyVotingTile).not.toBeNull();

      const currentlyVotingTileTitle = await within(currentlyVotingTile).queryByTestId('tile-title');
      expect(currentlyVotingTileTitle).not.toBeNull();
      expect(currentlyVotingTileTitle.textContent).toEqual('Current voting stats');

      const currentlyVotingTileSummary = await within(currentlyVotingTile).queryByTestId('tile-summary');
      expect(currentlyVotingTileSummary).not.toBeNull();
      expect(currentlyVotingTileSummary.textContent).toEqual(`${statsSum}`);

      const currentlyVotingItems = await within(currentlyVotingTile).queryAllByTestId('currently-voting-item');
      expect(currentlyVotingItems[0].textContent).toEqual(
        `${capitalize(stats[0][0].toLowerCase())} - ${getPercentage(
          voteStats.proposals[stats[0][0]]?.votes,
          statsSum
        ).toFixed(2)}%`
      );
      expect(currentlyVotingItems[1].textContent).toEqual(
        `${capitalize(stats[1][0].toLowerCase())} - ${getPercentage(
          voteStats.proposals[stats[1][0]]?.votes,
          statsSum
        ).toFixed(2)}%`
      );
      expect(currentlyVotingItems[2].textContent).toEqual(
        `${capitalize(stats[2][0].toLowerCase())} - ${getPercentage(
          voteStats.proposals[stats[2][0]]?.votes,
          statsSum
        ).toFixed(2)}%`
      );

      const currentlyVotingChart = await within(currentlyVotingTile).queryByTestId('pie-chart');
      expect(currentlyVotingChart).toBeInTheDocument();
      expect(mockPieChart.mock.lastCall[0]).toEqual({
        style: { height: '200px', width: '200px' },
        lineWidth: 32,
        data: statsItems.map(({ label, name }) => ({
          title: label,
          value: (voteStats.proposals?.[name as any] as unknown as ByCategory['proposals'])?.votes,
          color: proposalColorsMap[name],
        })),
      });
    });
  });
});

describe("For the event that hasn't finished yet", () => {
  beforeEach(() => {
    mockUseCardano.mockReturnValue(useCardanoMock);
    mockGetStats.mockReturnValue(voteStats);
  });
  afterEach(() => {
    jest.clearAllMocks();
    cleanup();
  });
  test('should render proper state', async () => {
    const history = createMemoryHistory({ initialEntries: [ROUTES.LEADERBOARD] });

    renderWithProviders(
      <CustomRouter history={history}>
        <Leaderboard />
      </CustomRouter>,
      { preloadedState: { user: { event: eventMock_active } as UserState } }
    );

    const statsSum = '--';
    const placeholder = '--';
    const stats = Object.entries(voteStats.proposals);

    await waitFor(async () => {
      expect(mockGetStats).not.toBeCalled();

      const leaderboardPage = await screen.queryByTestId('leaderboard-page');
      expect(leaderboardPage).not.toBeNull();

      const leaderboardTitle = await within(leaderboardPage).queryByTestId('leaderboard-title');
      expect(leaderboardTitle).not.toBeNull();
      expect(leaderboardTitle.textContent).toEqual('Leaderboard');

      const pollStatsTile = await within(leaderboardPage).queryByTestId('poll-stats-tile');
      expect(pollStatsTile).not.toBeNull();

      const pollStatsTileTitle = await within(pollStatsTile).queryByTestId('tile-title');
      expect(pollStatsTileTitle).not.toBeNull();
      expect(pollStatsTileTitle.textContent).toEqual('Poll stats');

      const pollStatsTileSummary = await within(pollStatsTile).queryByTestId('tile-summary');
      expect(pollStatsTileSummary).not.toBeNull();
      expect(pollStatsTileSummary.textContent).toEqual(`${statsSum}`);

      const pollStatsItems = await within(pollStatsTile).queryAllByTestId('poll-stats-item');
      expect(pollStatsItems[0].textContent).toEqual(`${capitalize(stats[0][0].toLowerCase())}${placeholder}`);
      expect(pollStatsItems[1].textContent).toEqual(`${capitalize(stats[1][0].toLowerCase())}${placeholder}`);
      expect(pollStatsItems[2].textContent).toEqual(`${capitalize(stats[2][0].toLowerCase())}${placeholder}`);

      const currentlyVotingTile = await within(leaderboardPage).queryByTestId('currently-voting-tile');
      expect(currentlyVotingTile).not.toBeNull();

      const currentlyVotingTileTitle = await within(currentlyVotingTile).queryByTestId('tile-title');
      expect(currentlyVotingTileTitle).not.toBeNull();
      expect(currentlyVotingTileTitle.textContent).toEqual('Current voting stats');

      const currentlyVotingTileSummary = await within(currentlyVotingTile).queryByTestId('tile-summary');
      expect(currentlyVotingTileSummary).not.toBeNull();
      expect(currentlyVotingTileSummary.textContent).toEqual(`${statsSum}`);

      const currentlyVotingItems = await within(currentlyVotingTile).queryAllByTestId('currently-voting-item');
      expect(currentlyVotingItems[0].textContent).toEqual(`${capitalize(stats[0][0].toLowerCase())}`);
      expect(currentlyVotingItems[1].textContent).toEqual(`${capitalize(stats[1][0].toLowerCase())}`);
      expect(currentlyVotingItems[2].textContent).toEqual(`${capitalize(stats[2][0].toLowerCase())}`);

      const currentlyVotingChart = await within(currentlyVotingTile).queryByTestId('pie-chart');
      expect(currentlyVotingChart).toBeInTheDocument();
      expect(mockPieChart.mock.lastCall[0]).toEqual({
        style: { height: '200px', width: '200px' },
        lineWidth: 32,
        data: [{ title: '', value: 1, color: '#BBBBBB' }],
      });
    });
  });
});
