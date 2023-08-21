/* eslint-disable no-var */
var mockVerifyVote = jest.fn();
var mockToast = jest.fn();
import React from 'react';
import '@testing-library/jest-dom';
import { expect } from '@jest/globals';
import { cleanup, act, waitFor } from '@testing-library/react';
import { createMemoryHistory } from 'history';
import BlockIcon from '@mui/icons-material/Block';
import { UserState } from 'common/store/types';
import { ROUTES } from 'common/routes';
import { Toast } from 'components/common/Toast/Toast';
import { renderWithProviders } from 'test/mockProviders';
import { CustomRouter } from 'test/CustomRouter';
import { VoteReceiptMock_Full_MediumAssurance } from 'test/mocks';
import { VoteReceipt } from '../VoteReceipt';

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

jest.mock('react-hot-toast', () => mockToast);

jest.mock('@cardano-foundation/cardano-connect-with-wallet', () => ({
  useCardano: jest.fn(),
  getWalletIcon: () => <span data-testid="getWalletIcon" />,
  ConnectWalletList: () => {
    return <span data-testid="connected-wallet-list" />;
  },
  ConnectWalletButton: () => {
    return <span data-testid="connected-wallet-button" />;
  },
}));

jest.mock('common/api/verificationService', () => ({
  ...jest.requireActual('common/api/verificationService'),
  verifyVote: mockVerifyVote,
}));

describe('Vote receipt:', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    cleanup();
  });
  describe('should handle error scenarios:', () => {
    test('should display proper message if fails to verify vote', async () => {
      mockVerifyVote.mockImplementation(async () => await Promise.reject('error'));

      const history = createMemoryHistory({ initialEntries: [ROUTES.INTRO] });
      await act(() =>
        renderWithProviders(
          <CustomRouter history={history}>
            <VoteReceipt
              setOpen={jest.fn()}
              fetchReceipt={jest.fn()}
            />
          </CustomRouter>,
          { preloadedState: { user: { receipt: VoteReceiptMock_Full_MediumAssurance } as UserState } }
        )
      );

      await waitFor(() => {
        expect(mockToast).toBeCalledWith(
          <Toast
            message="Unable to verify vote receipt. Please try again"
            error
            icon={<BlockIcon style={{ fontSize: '19px', color: '#F5F9FF' }} />}
          />
        );
      });
    });
  });

  describe('BASIC', () => {
    test.todo('should render proper state');
  });

  describe('PARTIAL', () => {
    test.todo('should render proper state');
  });

  describe('ROLLBACK', () => {
    test.todo('should render proper state');
  });

  describe('FULL', () => {
    describe('LOW', () => {
      test.todo('should render proper state');
    });
    describe('MEDIUM', () => {
      test.todo('should render proper state');
    });
    describe('HIGH', () => {
      test.todo('should render proper state');
    });
    describe('VERIFIED', () => {
      test.todo('should render proper state');
    });
    describe('verification modal', () => {
      test.todo('should render proper state');
    });
  });
});
