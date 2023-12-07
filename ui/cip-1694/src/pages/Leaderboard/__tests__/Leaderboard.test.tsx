/* eslint-disable @typescript-eslint/no-explicit-any */
/* eslint-disable no-var */
var mockUseCardano = jest.fn();
var mockGetStats = jest.fn();
var mockGetChainTip = jest.fn();
var mockPieChart = jest.fn();
var mockToast = jest.fn();
/* eslint-disable import/imports-first */
import 'whatwg-fetch';
import '@testing-library/jest-dom';
import BigNumber from 'bignumber.js';
import { expect } from '@jest/globals';
import { screen, within, waitFor, cleanup } from '@testing-library/react';
import BlockIcon from '@mui/icons-material/Block';
import React from 'react';
import { createMemoryHistory } from 'history';
import capitalize from 'lodash/capitalize';
import { ROUTES } from 'common/routes';
import { UserState } from 'common/store/types';
import { Toast } from 'components/Toast/Toast';
import { renderWithProviders } from 'test/mockProviders';
import { useCardanoMock, eventMock_finished, voteStats, eventMock_active, chainTipMock } from 'test/mocks';
import { CustomRouter } from 'test/CustomRouter';
import { Leaderboard } from '../Leaderboard';
import { proposalColorsMap, getPercentage, formatNumber, lovelacesToAdaString } from '../utils';

jest.mock('react-minimal-pie-chart', () => ({
  PieChart: mockPieChart,
}));

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

jest.mock('../../../env', () => {
  const original = jest.requireActual('../../../env');
  return {
    ...original,
    env: {
      ...original.env,
      CATEGORY_ID: 'CHANGE_GOV_STRUCTURE',
      EVENT_ID: 'CIP-1694_Pre_Ratification_3316',
      TARGET_NETWORK: 'Preprod',
    },
  };
});

jest.mock('common/api/leaderboardService', () => ({
  ...jest.requireActual('common/api/leaderboardService'),
  getStats: mockGetStats,
}));

jest.mock('common/api/voteService', () => ({
  ...jest.requireActual('common/api/voteService'),
  getChainTip: mockGetChainTip,
}));

jest.mock('react-hot-toast', () => ({
  __esModule: true,
  ...jest.requireActual('react-hot-toast'),
  default: mockToast,
}));

describe('For the event that has already finished', () => {
  beforeEach(() => {
    mockUseCardano.mockReturnValue(useCardanoMock);
    mockGetStats.mockReturnValue(voteStats);
    mockGetChainTip.mockReturnValue({ ...chainTipMock, epochNo: eventMock_finished.proposalsRevealEpoch + 1 });
  });
  afterEach(() => {
    jest.clearAllMocks();
    cleanup();
  });

  test('should display proper state for voting stats', async () => {
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
        ?.find(({ id }) => id === 'CHANGE_GOV_STRUCTURE')
        ?.proposals?.map(({ name, id }) => ({
          id,
          name,
          label: capitalize(name.toLowerCase()),
        })) || [];

    await waitFor(async () => {
      const leaderboardPage = screen.queryByTestId('leaderboard-page');
      expect(leaderboardPage).not.toBeNull();

      const leaderboardTitle = within(leaderboardPage).queryByTestId('leaderboard-title');
      expect(leaderboardTitle).not.toBeNull();
      expect(leaderboardTitle.textContent).toEqual('Leaderboard');

      const pollStatsTile = within(leaderboardPage).queryByTestId('poll-stats-tile-votes');
      expect(pollStatsTile).not.toBeNull();

      const pollStatsTileTitle = within(pollStatsTile).queryByTestId('tile-title');
      expect(pollStatsTileTitle).not.toBeNull();
      expect(pollStatsTileTitle.textContent).toEqual('Total number of ballot submissions ');

      const pollStatsTileSummary = within(pollStatsTile).queryByTestId('tile-summary');
      expect(pollStatsTileSummary).not.toBeNull();
      expect(pollStatsTileSummary.textContent).toEqual(formatNumber(statsSum));

      const pollStatsItems = within(pollStatsTile).queryAllByTestId('poll-stats-item-votes');
      for (const item in statsItems) {
        expect(pollStatsItems[item].textContent).toEqual(
          `${capitalize(statsItems[item].name.toLowerCase())}${formatNumber(stats[item][1].votes)}`
        );
      }

      const currentlyVotingTile = within(leaderboardPage).queryByTestId('currently-voting-tile-votes');
      expect(currentlyVotingTile).not.toBeNull();

      const currentlyVotingTileTitle = within(currentlyVotingTile).queryByTestId('tile-title');
      expect(currentlyVotingTileTitle).not.toBeNull();
      expect(currentlyVotingTileTitle.textContent).toEqual('Current ballot stats');

      const currentlyVotingTileSummary = within(currentlyVotingTile).queryByTestId('tile-summary');
      expect(currentlyVotingTileSummary).not.toBeNull();
      expect(currentlyVotingTileSummary.textContent).toEqual(formatNumber(statsSum));

      const currentlyVotingItems = within(currentlyVotingTile).queryAllByTestId('currently-voting-item-votes');
      for (const item in statsItems) {
        expect(currentlyVotingItems[item].textContent).toEqual(
          `${capitalize(statsItems[item].name.toLowerCase())} - ${getPercentage(
            voteStats.proposals[statsItems[item].id]?.votes,
            statsSum
          ).toFixed(2)}%`
        );
      }

      const currentlyVotingChart = within(currentlyVotingTile).queryByTestId('pie-chart');
      expect(currentlyVotingChart).toBeInTheDocument();
      expect(mockPieChart.mock.calls[mockPieChart.mock.calls.length - 2][0]).toEqual({
        style: { height: '200px', width: '200px' },
        lineWidth: 32,
        data: statsItems.map(({ label, name, id }) => ({
          title: label,
          value: getPercentage(voteStats.proposals?.[id as any]?.votes, statsSum),
          color: proposalColorsMap[name],
        })),
      });
    });
  });

  test('should display proper state for votingPower stats', async () => {
    mockPieChart.mockImplementation(() => <div data-testid="pie-chart" />);
    const history = createMemoryHistory({ initialEntries: [ROUTES.LEADERBOARD] });

    renderWithProviders(
      <CustomRouter history={history}>
        <Leaderboard />
      </CustomRouter>,
      { preloadedState: { user: { event: eventMock_finished } as UserState } }
    );

    const stats = Object.entries(voteStats.proposals);
    const statsSum = Object.values(voteStats.proposals)?.reduce(
      (acc, { votingPower }) => (acc = acc.add(votingPower)),
      new BigNumber(0)
    );
    const statsItems =
      eventMock_finished?.categories
        ?.find(({ id }) => id === 'CHANGE_GOV_STRUCTURE')
        ?.proposals?.map(({ name, id }) => ({
          id,
          name,
          label: capitalize(name.toLowerCase()),
        })) || [];

    await waitFor(async () => {
      const leaderboardPage = screen.queryByTestId('leaderboard-page');
      expect(leaderboardPage).not.toBeNull();

      const leaderboardTitle = within(leaderboardPage).queryByTestId('leaderboard-title');
      expect(leaderboardTitle).not.toBeNull();
      expect(leaderboardTitle.textContent).toEqual('Leaderboard');

      const pollStatsTile = within(leaderboardPage).queryByTestId('poll-stats-tile-votingPower');
      expect(pollStatsTile).not.toBeNull();

      const pollStatsTileTitle = within(pollStatsTile).queryByTestId('tile-title');
      expect(pollStatsTileTitle).not.toBeNull();
      expect(pollStatsTileTitle.textContent).toEqual('Total ballot power ');

      const pollStatsTileSummary = within(pollStatsTile).queryByTestId('tile-summary');
      expect(pollStatsTileSummary).not.toBeNull();
      expect(pollStatsTileSummary.textContent).toEqual(lovelacesToAdaString(statsSum));

      const pollStatsItems = within(pollStatsTile).queryAllByTestId('poll-stats-item-votingPower');
      for (const item in statsItems) {
        expect(pollStatsItems[item].textContent).toEqual(
          `${capitalize(statsItems[item].name.toLowerCase())}${lovelacesToAdaString(stats[item][1].votingPower)}`
        );
      }

      const currentlyVotingTile = within(leaderboardPage).queryByTestId('currently-voting-tile-votingPower');
      expect(currentlyVotingTile).not.toBeNull();

      const currentlyVotingTileTitle = within(currentlyVotingTile).queryByTestId('tile-title');
      expect(currentlyVotingTileTitle).not.toBeNull();
      expect(currentlyVotingTileTitle.textContent).toEqual('Voting power');

      const currentlyVotingTileSummary = within(currentlyVotingTile).queryByTestId('tile-summary');
      expect(currentlyVotingTileSummary).not.toBeNull();
      expect(currentlyVotingTileSummary.textContent).toEqual(lovelacesToAdaString(statsSum));

      const currentlyVotingItems = within(currentlyVotingTile).queryAllByTestId('currently-voting-item-votingPower');
      for (const item in statsItems) {
        expect(currentlyVotingItems[item].textContent).toEqual(
          `${capitalize(statsItems[item].name.toLowerCase())} - ${getPercentage(
            voteStats.proposals[statsItems[item].id]?.votingPower,
            statsSum.toString()
          ).toFixed(2)}%`
        );
      }

      const currentlyVotingChart = within(currentlyVotingTile).queryByTestId('pie-chart');
      expect(currentlyVotingChart).toBeInTheDocument();
      expect(mockPieChart.mock.calls[mockPieChart.mock.calls.length - 1][0]).toEqual({
        style: { height: '200px', width: '200px' },
        lineWidth: 32,
        data: statsItems.map(({ label, name, id }) => ({
          title: label,
          value: getPercentage(voteStats.proposals?.[id as any]?.votingPower, statsSum.toString()),
          color: proposalColorsMap[name],
        })),
      });
    });
  });

  test('should display proper error if getChainTip throws', async () => {
    const error = 'error';
    mockGetChainTip.mockReset();
    mockGetChainTip.mockImplementation(async () => await Promise.reject(error));

    const history = createMemoryHistory({ initialEntries: [ROUTES.LEADERBOARD] });

    renderWithProviders(
      <CustomRouter history={history}>
        <Leaderboard />
      </CustomRouter>,
      { preloadedState: { user: { event: eventMock_finished } as UserState } }
    );

    await waitFor(async () => {
      expect(mockToast).toBeCalledWith(
        <Toast
          message={'Failed to fetch chain tip'}
          error
          icon={<BlockIcon style={{ fontSize: '19px', color: '#F5F9FF' }} />}
        />
      );

      expect(mockGetStats).not.toHaveBeenCalled();
    });
  });

  test('should render proper state for voting stats if epochNo < proposalsRevealEpoch', async () => {
    mockPieChart.mockImplementation(() => <div data-testid="pie-chart" />);
    const history = createMemoryHistory({ initialEntries: [ROUTES.LEADERBOARD] });
    mockGetChainTip.mockReset();
    mockGetChainTip.mockReturnValue({ ...chainTipMock, epochNo: eventMock_finished.proposalsRevealEpoch - 1 });
    renderWithProviders(
      <CustomRouter history={history}>
        <Leaderboard />
      </CustomRouter>,
      { preloadedState: { user: { event: eventMock_finished } as UserState } }
    );

    const placeholder = '--';

    const statsItems =
      eventMock_finished?.categories
        ?.find(({ id }) => id === 'CHANGE_GOV_STRUCTURE')
        ?.proposals?.map(({ name, id }) => ({
          id,
          name,
          label: capitalize(name.toLowerCase()),
        })) || [];

    await waitFor(async () => {
      expect(mockGetStats).not.toBeCalled();

      const leaderboardPage = screen.queryByTestId('leaderboard-page');
      expect(leaderboardPage).not.toBeNull();

      const leaderboardTitle = within(leaderboardPage).queryByTestId('leaderboard-title');
      expect(leaderboardTitle).not.toBeNull();
      expect(leaderboardTitle.textContent).toEqual('Leaderboard');

      const pollStatsTile = within(leaderboardPage).queryByTestId('poll-stats-tile-votes');
      expect(pollStatsTile).not.toBeNull();

      const pollStatsTileTitle = within(pollStatsTile).queryByTestId('tile-title');
      expect(pollStatsTileTitle).not.toBeNull();
      expect(pollStatsTileTitle.textContent).toEqual('Total number of ballot submissions ');

      const pollStatsTileSummary = within(pollStatsTile).queryByTestId('tile-summary');
      expect(pollStatsTileSummary).not.toBeNull();
      expect(pollStatsTileSummary.textContent).toEqual(placeholder);

      const pollStatsItems = within(pollStatsTile).queryAllByTestId('poll-stats-item-votes');
      for (const item in statsItems) {
        expect(pollStatsItems[item].textContent).toEqual(
          `${capitalize(statsItems[item].name.toLowerCase())}${placeholder}`
        );
      }

      const currentlyVotingTile = within(leaderboardPage).queryByTestId('currently-voting-tile-votes');
      expect(currentlyVotingTile).not.toBeNull();

      const currentlyVotingTileTitle = within(currentlyVotingTile).queryByTestId('tile-title');
      expect(currentlyVotingTileTitle).not.toBeNull();
      expect(currentlyVotingTileTitle.textContent).toEqual('Current ballot stats');

      const currentlyVotingTileSummary = within(currentlyVotingTile).queryByTestId('tile-summary');
      expect(currentlyVotingTileSummary).not.toBeNull();
      expect(currentlyVotingTileSummary.textContent).toEqual(placeholder);

      const currentlyVotingItems = within(currentlyVotingTile).queryAllByTestId('currently-voting-item-votes');
      for (const item in statsItems) {
        expect(currentlyVotingItems[item].textContent).toEqual(`${capitalize(statsItems[item].name.toLowerCase())}`);
      }

      const currentlyVotingChart = within(currentlyVotingTile).queryByTestId('pie-chart');
      expect(currentlyVotingChart).toBeInTheDocument();
      expect(mockPieChart.mock.calls[mockPieChart.mock.calls.length - 2][0]).toEqual({
        style: { height: '200px', width: '200px' },
        lineWidth: 32,
        data: [{ title: '', value: 1, color: '#BBBBBB' }],
      });
    });
  });

  test('should render proper state for voterPower stats if epochNo < proposalsRevealEpoch', async () => {
    mockPieChart.mockImplementation(() => <div data-testid="pie-chart" />);
    const history = createMemoryHistory({ initialEntries: [ROUTES.LEADERBOARD] });
    mockGetChainTip.mockReset();
    mockGetChainTip.mockReturnValue({ ...chainTipMock, epochNo: eventMock_finished.proposalsRevealEpoch - 1 });
    renderWithProviders(
      <CustomRouter history={history}>
        <Leaderboard />
      </CustomRouter>,
      { preloadedState: { user: { event: eventMock_finished } as UserState } }
    );

    const placeholder = '--';

    const statsItems =
      eventMock_finished?.categories
        ?.find(({ id }) => id === 'CHANGE_GOV_STRUCTURE')
        ?.proposals?.map(({ name, id }) => ({
          id,
          name,
          label: capitalize(name.toLowerCase()),
        })) || [];

    await waitFor(async () => {
      expect(mockGetStats).not.toBeCalled();

      const leaderboardPage = screen.queryByTestId('leaderboard-page');
      expect(leaderboardPage).not.toBeNull();

      const leaderboardTitle = within(leaderboardPage).queryByTestId('leaderboard-title');
      expect(leaderboardTitle).not.toBeNull();
      expect(leaderboardTitle.textContent).toEqual('Leaderboard');

      const pollStatsTile = within(leaderboardPage).queryByTestId('poll-stats-tile-votingPower');
      expect(pollStatsTile).not.toBeNull();

      const pollStatsTileTitle = within(pollStatsTile).queryByTestId('tile-title');
      expect(pollStatsTileTitle).not.toBeNull();
      expect(pollStatsTileTitle.textContent).toEqual('Total ballot power ');

      const pollStatsTileSummary = within(pollStatsTile).queryByTestId('tile-summary');
      expect(pollStatsTileSummary).not.toBeNull();
      expect(pollStatsTileSummary.textContent).toEqual(placeholder);

      const pollStatsItems = within(pollStatsTile).queryAllByTestId('poll-stats-item-votingPower');
      for (const item in statsItems) {
        expect(pollStatsItems[item].textContent).toEqual(
          `${capitalize(statsItems[item].name.toLowerCase())}${placeholder}`
        );
      }

      const currentlyVotingTile = within(leaderboardPage).queryByTestId('currently-voting-tile-votingPower');
      expect(currentlyVotingTile).not.toBeNull();

      const currentlyVotingTileTitle = within(currentlyVotingTile).queryByTestId('tile-title');
      expect(currentlyVotingTileTitle).not.toBeNull();
      expect(currentlyVotingTileTitle.textContent).toEqual('Voting power');

      const currentlyVotingTileSummary = within(currentlyVotingTile).queryByTestId('tile-summary');
      expect(currentlyVotingTileSummary).not.toBeNull();
      expect(currentlyVotingTileSummary.textContent).toEqual(placeholder);

      const currentlyVotingItems = within(currentlyVotingTile).queryAllByTestId('currently-voting-item-votingPower');
      for (const item in statsItems) {
        expect(currentlyVotingItems[item].textContent).toEqual(`${capitalize(statsItems[item].name.toLowerCase())}`);
      }

      const currentlyVotingChart = within(currentlyVotingTile).queryByTestId('pie-chart');
      expect(currentlyVotingChart).toBeInTheDocument();
      expect(mockPieChart.mock.calls[mockPieChart.mock.calls.length - 1][0]).toEqual({
        style: { height: '200px', width: '200px' },
        lineWidth: 32,
        data: [{ title: '', value: 1, color: '#BBBBBB' }],
      });
    });
  });

  test('should display proper error if getStats throws', async () => {
    const error = 'error';
    mockGetStats.mockReset();
    mockGetStats.mockImplementation(async () => await Promise.reject(error));

    const history = createMemoryHistory({ initialEntries: [ROUTES.LEADERBOARD] });

    renderWithProviders(
      <CustomRouter history={history}>
        <Leaderboard />
      </CustomRouter>,
      { preloadedState: { user: { event: eventMock_finished } as UserState } }
    );

    await waitFor(async () => {
      expect(mockToast).toBeCalledWith(
        <Toast
          message={`Failed to fetch stats: ${error}`}
          error
          icon={<BlockIcon style={{ fontSize: '19px', color: '#F5F9FF' }} />}
        />
      );
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
  test('should render proper state for voting stats', async () => {
    mockPieChart.mockImplementation(() => <div data-testid="pie-chart" />);
    const history = createMemoryHistory({ initialEntries: [ROUTES.LEADERBOARD] });

    renderWithProviders(
      <CustomRouter history={history}>
        <Leaderboard />
      </CustomRouter>,
      { preloadedState: { user: { event: eventMock_active } as UserState } }
    );

    const placeholder = '--';

    const statsItems =
      eventMock_finished?.categories
        ?.find(({ id }) => id === 'CHANGE_GOV_STRUCTURE')
        ?.proposals?.map(({ name, id }) => ({
          id,
          name,
          label: capitalize(name.toLowerCase()),
        })) || [];

    await waitFor(async () => {
      expect(mockGetStats).not.toBeCalled();

      const leaderboardPage = screen.queryByTestId('leaderboard-page');
      expect(leaderboardPage).not.toBeNull();

      const leaderboardTitle = within(leaderboardPage).queryByTestId('leaderboard-title');
      expect(leaderboardTitle).not.toBeNull();
      expect(leaderboardTitle.textContent).toEqual('Leaderboard');

      const pollStatsTile = within(leaderboardPage).queryByTestId('poll-stats-tile-votes');
      expect(pollStatsTile).not.toBeNull();

      const pollStatsTileTitle = within(pollStatsTile).queryByTestId('tile-title');
      expect(pollStatsTileTitle).not.toBeNull();
      expect(pollStatsTileTitle.textContent).toEqual('Total number of ballot submissions ');

      const pollStatsTileSummary = within(pollStatsTile).queryByTestId('tile-summary');
      expect(pollStatsTileSummary).not.toBeNull();
      expect(pollStatsTileSummary.textContent).toEqual(placeholder);

      const pollStatsItems = within(pollStatsTile).queryAllByTestId('poll-stats-item-votes');
      for (const item in statsItems) {
        expect(pollStatsItems[item].textContent).toEqual(
          `${capitalize(statsItems[item].name.toLowerCase())}${placeholder}`
        );
      }

      const currentlyVotingTile = within(leaderboardPage).queryByTestId('currently-voting-tile-votes');
      expect(currentlyVotingTile).not.toBeNull();

      const currentlyVotingTileTitle = within(currentlyVotingTile).queryByTestId('tile-title');
      expect(currentlyVotingTileTitle).not.toBeNull();
      expect(currentlyVotingTileTitle.textContent).toEqual('Current ballot stats');

      const currentlyVotingTileSummary = within(currentlyVotingTile).queryByTestId('tile-summary');
      expect(currentlyVotingTileSummary).not.toBeNull();
      expect(currentlyVotingTileSummary.textContent).toEqual(placeholder);

      const currentlyVotingItems = within(currentlyVotingTile).queryAllByTestId('currently-voting-item-votes');
      for (const item in statsItems) {
        expect(currentlyVotingItems[item].textContent).toEqual(`${capitalize(statsItems[item].name.toLowerCase())}`);
      }

      const currentlyVotingChart = within(currentlyVotingTile).queryByTestId('pie-chart');
      expect(currentlyVotingChart).toBeInTheDocument();
      expect(mockPieChart.mock.lastCall[0]).toEqual({
        style: { height: '200px', width: '200px' },
        lineWidth: 32,
        data: [{ title: '', value: 1, color: '#BBBBBB' }],
      });
    });
  });

  test('should render proper state for votingPower stats', async () => {
    mockPieChart.mockImplementation(() => <div data-testid="pie-chart" />);
    const history = createMemoryHistory({ initialEntries: [ROUTES.LEADERBOARD] });

    renderWithProviders(
      <CustomRouter history={history}>
        <Leaderboard />
      </CustomRouter>,
      { preloadedState: { user: { event: eventMock_active } as UserState } }
    );

    const placeholder = '--';

    const statsItems =
      eventMock_finished?.categories
        ?.find(({ id }) => id === 'CHANGE_GOV_STRUCTURE')
        ?.proposals?.map(({ name, id }) => ({
          id,
          name,
          label: capitalize(name.toLowerCase()),
        })) || [];

    await waitFor(async () => {
      expect(mockGetStats).not.toBeCalled();

      const leaderboardPage = screen.queryByTestId('leaderboard-page');
      expect(leaderboardPage).not.toBeNull();

      const leaderboardTitle = within(leaderboardPage).queryByTestId('leaderboard-title');
      expect(leaderboardTitle).not.toBeNull();
      expect(leaderboardTitle.textContent).toEqual('Leaderboard');

      const pollStatsTile = within(leaderboardPage).queryByTestId('poll-stats-tile-votingPower');
      expect(pollStatsTile).not.toBeNull();

      const pollStatsTileTitle = within(pollStatsTile).queryByTestId('tile-title');
      expect(pollStatsTileTitle).not.toBeNull();
      expect(pollStatsTileTitle.textContent).toEqual('Total ballot power ');

      const pollStatsTileSummary = within(pollStatsTile).queryByTestId('tile-summary');
      expect(pollStatsTileSummary).not.toBeNull();
      expect(pollStatsTileSummary.textContent).toEqual(placeholder);

      const pollStatsItems = within(pollStatsTile).queryAllByTestId('poll-stats-item-votingPower');
      for (const item in statsItems) {
        expect(pollStatsItems[item].textContent).toEqual(
          `${capitalize(statsItems[item].name.toLowerCase())}${placeholder}`
        );
      }

      const currentlyVotingTile = within(leaderboardPage).queryByTestId('currently-voting-tile-votingPower');
      expect(currentlyVotingTile).not.toBeNull();

      const currentlyVotingTileTitle = within(currentlyVotingTile).queryByTestId('tile-title');
      expect(currentlyVotingTileTitle).not.toBeNull();
      expect(currentlyVotingTileTitle.textContent).toEqual('Voting power');

      const currentlyVotingTileSummary = within(currentlyVotingTile).queryByTestId('tile-summary');
      expect(currentlyVotingTileSummary).not.toBeNull();
      expect(currentlyVotingTileSummary.textContent).toEqual(placeholder);

      const currentlyVotingItems = within(currentlyVotingTile).queryAllByTestId('currently-voting-item-votingPower');
      for (const item in statsItems) {
        expect(currentlyVotingItems[item].textContent).toEqual(`${capitalize(statsItems[item].name.toLowerCase())}`);
      }

      const currentlyVotingChart = within(currentlyVotingTile).queryByTestId('pie-chart');
      expect(currentlyVotingChart).toBeInTheDocument();
      expect(mockPieChart.mock.lastCall[0]).toEqual({
        style: { height: '200px', width: '200px' },
        lineWidth: 32,
        data: [{ title: '', value: 1, color: '#BBBBBB' }],
      });
    });
  });
});
