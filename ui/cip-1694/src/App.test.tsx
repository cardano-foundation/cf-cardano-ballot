/* eslint-disable no-var */
var mockHeader = jest.fn();
var mockContent = jest.fn();
var mockFooter = jest.fn();
var mockGetEvent = jest.fn();
var mockToast = jest.fn();
import React from 'react';
import '@testing-library/jest-dom';
import { expect } from '@jest/globals';
import { screen, within, cleanup, waitFor } from '@testing-library/react';
import { eventMock_active } from 'test/mocks';
import { renderWithProviders } from 'test/mockProviders';
import { Toast } from 'components/common/Toast/Toast';
import BlockIcon from '@mui/icons-material/Block';
import { App } from './App';

jest.mock('./components/common/Header/Header', () => ({
  Header: mockHeader,
}));

jest.mock('./components/common/Content/Content', () => ({
  Content: mockContent,
}));

jest.mock('./components/common/Footer/Footer', () => ({
  Footer: mockFooter,
}));

jest.mock('common/api/referenceDataService', () => ({
  ...jest.requireActual('common/api/referenceDataService'),
  getEvent: mockGetEvent,
}));

jest.mock('react-hot-toast', () => ({ __esModule: true, ...jest.requireActual('react-hot-toast'), default: mockToast }));

describe('App', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    cleanup();
    mockHeader.mockImplementation(() => <span data-testid="header"></span>);
    mockContent.mockImplementation(() => <span data-testid="content"></span>);
    mockFooter.mockImplementation(() => <span data-testid="footer"></span>);
  });

  test('should render proper state', async () => {
    renderWithProviders(<App />);

    const layout = await screen.findByTestId('layout');
    expect(within(layout).queryByTestId('header')).toBeInTheDocument();
    expect(within(layout).queryByTestId('content')).toBeInTheDocument();
    expect(within(layout).queryByTestId('footer')).toBeInTheDocument();
  });

  test('should fetch an event', async () => {
    mockGetEvent.mockReturnValueOnce(eventMock_active);

    const { store } = renderWithProviders(<App />);

    await waitFor(async () => {
      expect(mockGetEvent).toBeCalledTimes(1);
      expect(store.getState().user.event).toEqual(eventMock_active);
    });
  });

  test('should display toast if fetch event request failed', async () => {
    mockGetEvent.mockImplementation(async () => await Promise.reject('error'));

    renderWithProviders(<App />);

    await waitFor(async () => {
      expect(mockToast).toBeCalledWith(
        <Toast
          message="Failed to fetch event"
          error
          icon={<BlockIcon style={{ fontSize: '19px', color: '#F5F9FF' }} />}
        />
      );
    });
  });
});
