import React from 'react';
import '@testing-library/jest-dom';
import { expect } from '@jest/globals';
import { cleanup, act, screen, waitFor, fireEvent } from '@testing-library/react';
import { createMemoryHistory } from 'history';
import { ROUTES } from 'common/routes';
import { renderWithProviders } from 'test/mockProviders';
import { CustomRouter } from 'test/CustomRouter';
import { VoteReceiptMock_Full_HighAssurance } from 'test/mocks';
import { ReceiptInfo } from '../ReceiptInfo';

jest.mock('@cardano-foundation/cardano-connect-with-wallet', () => ({
  useCardano: jest.fn(),
  NetworkType: {
    MAINNET: 'mainnet',
    TESTNET: 'testnet',
  },
  getWalletIcon: () => <span data-testid="getWalletIcon" />,
  ConnectWalletList: () => {
    return <span data-testid="connected-wallet-list" />;
  },
  ConnectWalletButton: () => {
    return <span data-testid="connected-wallet-button" />;
  },
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

describe('ReceiptInfo:', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    cleanup();
  });

  test('should not render if there is no receipt', async () => {
    const history = createMemoryHistory({ initialEntries: [ROUTES.INTRO] });
    await act(async () =>
      renderWithProviders(
        <CustomRouter history={history}>
          <ReceiptInfo
            fetchReceipt={jest.fn()}
            receipt={undefined}
            isVerified
          />
        </CustomRouter>
      )
    );

    await waitFor(async () => {
      expect(screen.queryByTestId('receipt-info')).toBeNull();
    });
  });

  test('should not refetch receipt if receipt is already verified', async () => {
    const fetchReceiptMock = jest.fn();
    const history = createMemoryHistory({ initialEntries: [ROUTES.INTRO] });
    await act(async () =>
      renderWithProviders(
        <CustomRouter history={history}>
          <ReceiptInfo
            fetchReceipt={fetchReceiptMock}
            receipt={VoteReceiptMock_Full_HighAssurance}
            isVerified
          />
        </CustomRouter>
      )
    );

    fireEvent.click(screen.queryByTestId('refetch-receipt-button'));

    await waitFor(async () => {
      expect(fetchReceiptMock).not.toBeCalled();
    });
  });
});
