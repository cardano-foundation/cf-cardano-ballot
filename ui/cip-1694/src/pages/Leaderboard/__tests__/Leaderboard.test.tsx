/* eslint-disable @typescript-eslint/no-explicit-any */
/* eslint-disable no-var */
var mockUseCardano = jest.fn();
var mockGetStats = jest.fn();
var mockPieChart = jest.fn();
var mockToast = jest.fn();
/* eslint-disable import/imports-first */
import 'whatwg-fetch';
import '@testing-library/jest-dom';
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
import { useCardanoMock, eventMock_finished, voteStats, eventMock_active } from 'test/mocks';
import { CustomRouter } from 'test/CustomRouter';
import { ByProposalsInCategoryStats } from 'types/voting-app-types';
import { Leaderboard } from '../Leaderboard';
import { proposalColorsMap, getPercentage } from '../utils';

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

jest.mock('react-hot-toast', () => ({
  __esModule: true,
  ...jest.requireActual('react-hot-toast'),
  default: mockToast,
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

      const pollStatsTile = within(leaderboardPage).queryByTestId('poll-stats-tile');
      expect(pollStatsTile).not.toBeNull();

      const pollStatsTileTitle = within(pollStatsTile).queryByTestId('tile-title');
      expect(pollStatsTileTitle).not.toBeNull();
      expect(pollStatsTileTitle.textContent).toEqual('Poll stats');

      const pollStatsTileSummary = within(pollStatsTile).queryByTestId('tile-summary');
      expect(pollStatsTileSummary).not.toBeNull();
      expect(pollStatsTileSummary.textContent).toEqual(`${statsSum}`);

      const pollStatsItems = within(pollStatsTile).queryAllByTestId('poll-stats-item');
      for (const item in statsItems) {
        expect(pollStatsItems[item].textContent).toEqual(
          `${capitalize(statsItems[item].name.toLowerCase())}${stats[item][1].votes}`
        );
      }

      const currentlyVotingTile = within(leaderboardPage).queryByTestId('currently-voting-tile');
      expect(currentlyVotingTile).not.toBeNull();

      const currentlyVotingTileTitle = within(currentlyVotingTile).queryByTestId('tile-title');
      expect(currentlyVotingTileTitle).not.toBeNull();
      expect(currentlyVotingTileTitle.textContent).toEqual('Current voting stats');

      const currentlyVotingTileSummary = within(currentlyVotingTile).queryByTestId('tile-summary');
      expect(currentlyVotingTileSummary).not.toBeNull();
      expect(currentlyVotingTileSummary.textContent).toEqual(`${statsSum}`);

      const currentlyVotingItems = within(currentlyVotingTile).queryAllByTestId('currently-voting-item');
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
      expect(mockPieChart.mock.lastCall[0]).toEqual({
        style: { height: '200px', width: '200px' },
        lineWidth: 32,
        data: statsItems.map(({ label, name, id }) => ({
          title: label,
          value: (voteStats.proposals?.[id as any] as unknown as ByProposalsInCategoryStats['proposals'])?.votes,
          color: proposalColorsMap[name],
        })),
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

      const pollStatsTile = within(leaderboardPage).queryByTestId('poll-stats-tile');
      expect(pollStatsTile).not.toBeNull();

      const pollStatsTileTitle = within(pollStatsTile).queryByTestId('tile-title');
      expect(pollStatsTileTitle).not.toBeNull();
      expect(pollStatsTileTitle.textContent).toEqual('Poll stats');

      const pollStatsTileSummary = within(pollStatsTile).queryByTestId('tile-summary');
      expect(pollStatsTileSummary).not.toBeNull();
      expect(pollStatsTileSummary.textContent).toEqual(`${statsSum}`);

      const pollStatsItems = within(pollStatsTile).queryAllByTestId('poll-stats-item');
      for (const item in statsItems) {
        expect(pollStatsItems[item].textContent).toEqual(
          `${capitalize(statsItems[item].name.toLowerCase())}${placeholder}`
        );
      }

      const currentlyVotingTile = within(leaderboardPage).queryByTestId('currently-voting-tile');
      expect(currentlyVotingTile).not.toBeNull();

      const currentlyVotingTileTitle = within(currentlyVotingTile).queryByTestId('tile-title');
      expect(currentlyVotingTileTitle).not.toBeNull();
      expect(currentlyVotingTileTitle.textContent).toEqual('Current voting stats');

      const currentlyVotingTileSummary = within(currentlyVotingTile).queryByTestId('tile-summary');
      expect(currentlyVotingTileSummary).not.toBeNull();
      expect(currentlyVotingTileSummary.textContent).toEqual(`${statsSum}`);

      const currentlyVotingItems = within(currentlyVotingTile).queryAllByTestId('currently-voting-item');
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
