/* eslint-disable no-var */
var ToasterMock = jest.fn();
import React from 'react';
import '@testing-library/jest-dom';
import { expect } from '@jest/globals';
import { screen, cleanup, render, waitFor, act } from '@testing-library/react';
import { App } from './App';

jest.mock('common/routes', () => ({
  PageRoutes: () => <span data-testid="page-routes" />,
}));

jest.mock('react-hot-toast', () => ({
  Toaster: ToasterMock,
}));

describe('App', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    cleanup();
  });

  test('should render proper state', async () => {
    ToasterMock.mockReset();
    ToasterMock.mockImplementation(() => <span data-testid="toaster" />);
    await act(async () => {
      render(<App />);
    });

    await waitFor(async () => {
      expect(screen.queryByTestId('page-routes')).not.toBeNull();
      expect(screen.queryByTestId('toaster')).not.toBeNull();
      expect(ToasterMock).toBeCalledWith({ toastOptions: { className: undefined } }, {});
    });
  });
});
