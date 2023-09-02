/* eslint-disable no-var */
var mockConnectWalletList = jest.fn();
var mockToast = jest.fn();
var mockSupportedWallets = ['Wallet1', 'Wallet2'];
import React from 'react';
import '@testing-library/jest-dom';
import { expect } from '@jest/globals';
import { screen, within, cleanup, act } from '@testing-library/react';
import { createMemoryHistory } from 'history';
import BlockIcon from '@mui/icons-material/Block';
import { UserState } from 'common/store/types';
import { ROUTES } from 'common/routes';
import { Toast } from 'components/common/Toast/Toast';
import { Content } from '../Content';
import { renderWithProviders } from '../../../../test/mockProviders';
import { CustomRouter } from '../../../../test/CustomRouter';

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

jest.mock('../../../../env', () => {
  const original = jest.requireActual('../../../../env');
  return {
    ...original,
    env: {
      ...original.env,
      CATEGORY_ID: 'CIP-1694_Pre_Ratification_4619',
      EVENT_ID: 'CIP-1694_Pre_Ratification_4619',
      SUPPORTED_WALLETS: mockSupportedWallets,
    },
  };
});

jest.mock('@cardano-foundation/cardano-connect-with-wallet', () => ({
  useCardano: jest.fn(),
  getWalletIcon: () => <span data-testid="getWalletIcon" />,
  ConnectWalletList: mockConnectWalletList,
  ConnectWalletButton: () => {
    return <span data-testid="connected-wallet-button" />;
  },
}));

jest.mock('@mui/material', () => ({
  ...jest.requireActual('@mui/material'),
  debounce: jest.fn((fn) => fn),
}));

describe('ConnectWalletModal', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    cleanup();
  });

  test('should display proper state for connect wallet modal, properly react to wallet on connect error scenario', async () => {
    mockConnectWalletList.mockImplementation(() => {
      return <span data-testid="connect-wallet-list" />;
    });

    const history = createMemoryHistory({ initialEntries: [ROUTES.INTRO] });
    renderWithProviders(
      <CustomRouter history={history}>
        <Content />
      </CustomRouter>,
      { preloadedState: { user: { isConnectWalletModalVisible: true } as UserState } }
    );

    const modal = await screen.findByTestId('connected-wallet-modal');
    expect(modal).not.toBeNull();

    expect(within(modal).queryByTestId('connected-wallet-modal-title')).toHaveTextContent('Connect wallet');
    expect(within(modal).queryByTestId('connected-wallet-modal-description')).toHaveTextContent(
      'In order to vote, first you will need to connect your wallet.'
    );

    expect(within(modal).queryByTestId('connect-wallet-list')).not.toBeNull();
    await act(async () => {
      mockConnectWalletList.mock.lastCall[0].onConnectError();
    });

    expect(mockToast).toBeCalledWith(
      <Toast
        error={true}
        icon={<BlockIcon style={{ color: '#F5F9FF', fontSize: '19px' }} />}
        message="Unable to connect your wallet. Please try again"
      />
    );
  });

  test('should properly react to wallet on connect scenario', async () => {
    mockConnectWalletList.mockImplementation(() => {
      return <span data-testid="connect-wallet-list" />;
    });

    const history = createMemoryHistory({ initialEntries: [ROUTES.INTRO] });
    const { store } = renderWithProviders(
      <CustomRouter history={history}>
        <Content />
      </CustomRouter>,
      { preloadedState: { user: { isConnectWalletModalVisible: true } as UserState } }
    );

    await act(async () => {
      mockConnectWalletList.mock.lastCall[0].onConnect();
    });

    expect(mockToast).toBeCalledWith(<Toast message="Wallet Connected!" />);
    expect(store.getState().user.isConnectWalletModalVisible).toBeFalsy();
  });
});
