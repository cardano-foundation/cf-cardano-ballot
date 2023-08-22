/* eslint-disable no-var */
var mockIntroductionPage = jest.fn();
var mockVotePage = jest.fn();
var mockLeaderboard = jest.fn();
import React from 'react';
import '@testing-library/jest-dom';

import { expect } from '@jest/globals';
import { screen, cleanup, render } from '@testing-library/react';
import { CustomRouter } from 'test/CustomRouter';
import { createMemoryHistory } from 'history';
import { PageRoutes, ROUTES } from '../index';

jest.mock('pages/Introduction/Introduction', () => ({
  IntroductionPage: mockIntroductionPage,
}));

jest.mock('pages/Vote/Vote', () => ({
  VotePage: mockVotePage,
}));

jest.mock('pages/Leaderboard/Leaderboard', () => ({
  Leaderboard: mockLeaderboard,
}));

const url = ROUTES.INTRO;
Object.defineProperty(window, 'location', {
  value: {
    href: url,
  },
  writable: true,
});

describe('routes: ', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    cleanup();
    mockIntroductionPage.mockImplementation(() => <span data-testid="intro"></span>);
    mockVotePage.mockImplementation(() => <span data-testid="vote"></span>);
    mockLeaderboard.mockImplementation(() => <span data-testid="leaderboard"></span>);
  });

  test('should render intro page', async () => {
    const history = createMemoryHistory({ initialEntries: [ROUTES.INTRO] });
    render(
      <CustomRouter history={history}>
        <PageRoutes />
      </CustomRouter>
    );
    expect(await screen.findByTestId('intro')).toBeInTheDocument();
  });

  test('should render vote page', async () => {
    window.location.href = ROUTES.VOTE;
    const history = createMemoryHistory({ initialEntries: [ROUTES.INTRO] });
    render(
      <CustomRouter history={history}>
        <PageRoutes />
      </CustomRouter>
    );
    expect(await screen.findByTestId('intro')).toBeInTheDocument();
  });

  test('should render leaderboard page', async () => {
    window.location.href = ROUTES.LEADERBOARD;
    const history = createMemoryHistory({ initialEntries: [ROUTES.INTRO] });
    render(
      <CustomRouter history={history}>
        <PageRoutes />
      </CustomRouter>
    );
    expect(await screen.findByTestId('intro')).toBeInTheDocument();
  });
});
