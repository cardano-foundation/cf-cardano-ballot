/* eslint-disable no-var */
var VerifyVoteMock = jest.fn();
var NavigateMock = jest.fn();
import React from 'react';
import '@testing-library/jest-dom';
import { cleanup, render, screen, act } from '@testing-library/react';
import { createMemoryHistory } from 'history';
import { CustomRouter } from 'test/CustomRouter';
import { PageRoutes, ROUTES } from '..';

jest.mock('pages/VerifyVote/VerifyVote', () => ({
  VerifyVote: VerifyVoteMock,
}));

jest.mock('react-router-dom', () => ({
  ...jest.requireActual('react-router-dom'),
  Navigate: NavigateMock,
}));

describe('PageRoutes', () => {
  jest.setTimeout(5000);
  beforeEach(() => {
    jest.clearAllMocks();
    cleanup();
  });

  test('should render main route', async () => {
    VerifyVoteMock.mockReset();
    VerifyVoteMock.mockImplementation(() => <span data-testid="verify-vote-page" />);

    render(
      <CustomRouter history={createMemoryHistory({ initialEntries: [ROUTES.MAIN] })}>
        <PageRoutes />
      </CustomRouter>
    );

    expect(screen.queryByTestId('verify-vote-page')).toBeInTheDocument();
  });

  test('should redirect to main route', async () => {
    VerifyVoteMock.mockReset();
    VerifyVoteMock.mockImplementation(() => <span data-testid="verify-vote-page" />);
    NavigateMock.mockReset();
    NavigateMock.mockImplementation(() => <span data-testid="navigate-component" />);

    await act(async () => {
      render(
        <CustomRouter history={createMemoryHistory({ initialEntries: ['/non-existing-route'] })}>
          <PageRoutes />
        </CustomRouter>
      );
    });

    expect(screen.queryByTestId('navigate-component')).toBeInTheDocument();
    expect(NavigateMock).toBeCalledWith({ to: ROUTES.MAIN }, {});
  });
});
