/* eslint-disable no-var */
/* eslint-disable @typescript-eslint/no-explicit-any */
var mockUseCardano = jest.fn();
var mockToast = jest.fn();
var mockGetChainTip = jest.fn();
var mockResultsCommingSoonModal = jest.fn();
/* eslint-disable no-var */
import '@testing-library/jest-dom';
import React, { useEffect } from 'react';
import { expect } from '@jest/globals';
import BlockIcon from '@mui/icons-material/Block';
import { waitFor, cleanup } from '@testing-library/react';
import { createMemoryHistory } from 'history';
import { UserState } from 'common/store/types';
import { ROUTES } from 'common/routes';
import { renderWithProviders } from 'test/mockProviders';
import { eventMock_active, useCardanoMock } from 'test/mocks';
import { CustomRouter } from 'test/CustomRouter';
import { Toast } from 'components/common/Toast/Toast';
import { HeaderActions } from '../HeaderActions';

jest.mock('@cardano-foundation/cardano-connect-with-wallet', () => ({
  useCardano: mockUseCardano,
  getWalletIcon: () => <span data-testid="getWalletIcon" />,
  ConnectWalletList: () => {
    return <span data-testid="connect-wallet-list" />;
  },
  ConnectWalletButton: () => {
    return <span data-testid="connect-wallet-button" />;
  },
}));

jest.mock('pages/Leaderboard/components/ResultsCommingSoonModal/ResultsCommingSoonModal', () => ({
  ResultsCommingSoonModal: mockResultsCommingSoonModal,
}));

jest.mock('swiper/react', () => ({}));
jest.mock('swiper', () => ({}));

jest.mock('react-hot-toast', () => ({
  __esModule: true,
  ...jest.requireActual('react-hot-toast'),
  default: mockToast,
}));

jest.mock('common/api/voteService', () => ({
  ...jest.requireActual('common/api/voteService'),
  getChainTip: mockGetChainTip,
}));

describe('HeaderActions', () => {
  beforeEach(() => {
    mockUseCardano.mockReturnValue(useCardanoMock);
  });
  afterEach(() => {
    jest.clearAllMocks();
    cleanup();
  });

  test('should call onClick handler if provided', async () => {
    mockResultsCommingSoonModal.mockReset();
    mockResultsCommingSoonModal.mockImplementation(({ onConfirmFn }: { onConfirmFn: () => void }) => {
      useEffect(() => {
        onConfirmFn();
        // eslint-disable-next-line react-hooks/exhaustive-deps
      }, []);
      return <span></span>;
    });
    const onClick = jest.fn();
    const history = createMemoryHistory({ initialEntries: [ROUTES.INTRO] });
    renderWithProviders(
      <CustomRouter history={history}>
        <HeaderActions
          showNavigationItems
          onClick={onClick}
        />
      </CustomRouter>,
      { preloadedState: { user: { event: eventMock_active } as UserState } }
    );

    await waitFor(async () => {
      expect(onClick).toBeCalled();
    });
  });

  test('should display toast if fetch chain tip request failed', async () => {
    const error = 'error';
    mockGetChainTip.mockReset();
    mockGetChainTip.mockImplementation(async () => await Promise.reject(error));
    const history = createMemoryHistory({ initialEntries: [ROUTES.INTRO] });
    renderWithProviders(
      <CustomRouter history={history}>
        <HeaderActions />
      </CustomRouter>,
      { preloadedState: { user: { event: eventMock_active } as UserState } }
    );

    await waitFor(async () => {
      expect(mockToast).toBeCalledWith(
        <Toast
          message={'Failed to fecth chain tip'}
          error
          icon={<BlockIcon style={{ fontSize: '19px', color: '#F5F9FF' }} />}
        />
      );
    });

    const errorMessage = { message: 'errorMessage' };
    mockGetChainTip.mockReset();
    mockGetChainTip.mockImplementation(async () => await Promise.reject(errorMessage));
    renderWithProviders(
      <CustomRouter history={history}>
        <HeaderActions />
      </CustomRouter>,
      { preloadedState: { user: { event: eventMock_active } as UserState } }
    );

    await waitFor(async () => {
      expect(mockToast).toBeCalledWith(
        <Toast
          message={'Failed to fecth chain tip'}
          error
          icon={<BlockIcon style={{ fontSize: '19px', color: '#F5F9FF' }} />}
        />
      );
    });

    mockGetChainTip.mockReset();
    mockGetChainTip.mockImplementation(async () => await Promise.reject({}));
    renderWithProviders(
      <CustomRouter history={history}>
        <HeaderActions />
      </CustomRouter>,
      { preloadedState: { user: { event: eventMock_active } as UserState } }
    );

    await waitFor(async () => {
      expect(mockToast).toBeCalledWith(
        <Toast
          message={'Failed to fecth chain tip'}
          error
          icon={<BlockIcon style={{ fontSize: '19px', color: '#F5F9FF' }} />}
        />
      );
    });
  });
});
