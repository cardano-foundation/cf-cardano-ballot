/* eslint-disable @typescript-eslint/no-empty-function */
/* eslint-disable @typescript-eslint/no-explicit-any */
/* eslint-disable no-var */
var mockUseCardano = jest.fn();
var mockCastAVoteWithDigitalSignature = jest.fn();
var mockGetVotingPower = jest.fn();
var mockBuildCanonicalVoteInputJson = jest.fn();
var mockGetSignedMessagePromise = jest.fn();
var mockGetSlotNumber = jest.fn();
var mockGetVoteReceipt = jest.fn();
var mockToast = jest.fn();
/* eslint-disable import/imports-first */
import 'whatwg-fetch';
import '@testing-library/jest-dom';
import { expect } from '@jest/globals';
import { screen, within, waitFor, cleanup, fireEvent } from '@testing-library/react';
import React from 'react';
import { createMemoryHistory } from 'history';
import { rest } from 'msw';
import { setupServer } from 'msw/node';
import BlockIcon from '@mui/icons-material/Block';
import { ROUTES } from 'common/routes';
import { UserState } from 'common/store/types';
import { EVENT_BY_ID_REFERENCE_URL } from 'common/api/referenceDataService';
import { VotePage } from 'pages/Vote/Vote';
import { Toast } from 'components/common/Toast/Toast';
import { VERIFICATION_URL } from 'common/api/verificationService';
import { formatUTCDate, getDateAndMonth } from 'common/utils/dateUtils';
import { renderWithProviders } from 'test/mockProviders';
import {
  eventMock_active,
  chainTipMock,
  useCardanoMock,
  useCardanoMock_notConnected,
  accountDataMock,
  canonicalVoteInputJsonMock,
  VoteReceiptMock_Basic,
  eventMock_notStarted,
  eventMock_finished,
  VoteReceiptMock_Full_MediumAssurance,
} from 'test/mocks';
import { CustomRouter } from 'test/CustomRouter';
import { env } from '../../../env';

jest.mock('react-hot-toast', () => mockToast);

jest.mock('@cardano-foundation/cardano-connect-with-wallet', () => {
  return {
    useCardano: mockUseCardano,
    getWalletIcon: () => <span data-testid="getWalletIcon" />,
    ConnectWalletList: () => {
      return <span data-testid="ConnectWalletList" />;
    },
    ConnectWalletButton: () => {
      return <span data-testid="ConnectWalletButton" />;
    },
  };
});

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

jest.mock('../../../env', () => {
  const original = jest.requireActual('../../../env');
  return {
    ...original,
    env: {
      ...original.env,
      CATEGORY_ID: 'CIP-1694_Pre_Ratification_4619',
      EVENT_ID: 'CIP-1694_Pre_Ratification_4619',
    },
  };
});

jest.mock('common/api/voteService', () => ({
  ...jest.requireActual('common/api/voteService'),
  castAVoteWithDigitalSignature: mockCastAVoteWithDigitalSignature,
  getVotingPower: mockGetVotingPower,
  getSlotNumber: mockGetSlotNumber,
  getVoteReceipt: mockGetVoteReceipt,
}));

jest.mock('common/utils/voteUtils', () => ({
  ...jest.requireActual('common/utils/voteUtils'),
  buildCanonicalVoteInputJson: mockBuildCanonicalVoteInputJson,
  getSignedMessagePromise: mockGetSignedMessagePromise,
}));

export const handlers = [
  rest.get(`${EVENT_BY_ID_REFERENCE_URL}/${env.EVENT_ID}`, (req, res, ctx) => {
    return res(ctx.json(eventMock_active), ctx.delay(150));
  }),
  rest.post(`${VERIFICATION_URL}`, (req, res, ctx) => {
    return res(ctx.json(true), ctx.delay(150));
  }),
];

const server = setupServer(...handlers);

beforeAll(() => server.listen());
afterEach(() => server.resetHandlers());
afterAll(() => server.close());

describe('For ongoing event:', () => {
  beforeEach(() => {
    mockUseCardano.mockReturnValue(useCardanoMock);
    mockGetSlotNumber.mockReturnValue(chainTipMock);
    mockGetVoteReceipt.mockReturnValue({});
  });
  afterEach(() => {
    jest.clearAllMocks();
    cleanup();
  });

  test('should display proper state', async () => {
    mockUseCardano.mockReset();
    mockUseCardano.mockReturnValue(useCardanoMock_notConnected);

    const history = createMemoryHistory({ initialEntries: [ROUTES.VOTE] });

    renderWithProviders(
      <CustomRouter history={history}>
        <VotePage />
      </CustomRouter>,
      { preloadedState: { user: { event: eventMock_active } as UserState } }
    );

    await waitFor(async () => {
      const votePage = await screen.queryByTestId('vote-page');
      expect(votePage).not.toBeNull();

      const eventTitle = await within(votePage).queryByTestId('event-title');
      expect(eventTitle).not.toBeNull();
      expect(eventTitle.textContent).toEqual('CIP-1694 Vote');

      const eventTime = await within(votePage).queryByTestId('event-time');
      expect(eventTime).not.toBeNull();
      expect(eventTime.textContent).toEqual(`Voting closes: ${formatUTCDate(eventMock_active.eventEnd.toString())}`);

      const eventDescription = await within(votePage).queryByTestId('event-description');
      expect(eventDescription).not.toBeNull();
      expect(eventDescription.textContent).toEqual('(..)');

      const options = await within(votePage).queryAllByTestId('option-card');
      expect(options.length).toEqual(eventMock_active.categories[0].proposals.length);
      expect(options[0].textContent).toEqual(eventMock_active.categories[0].proposals[0].presentationName);
      expect(options[1].textContent).toEqual(eventMock_active.categories[0].proposals[1].presentationName);
      expect(options[2].textContent).toEqual(eventMock_active.categories[0].proposals[2].presentationName);

      const cta = await within(votePage).queryByTestId('proposal-connect-button');
      expect(cta).not.toBeNull();
      expect(cta.textContent).toEqual('Connect wallet to vote');
    });
  });

  test('should render connect wallet button and open connect wallet modal once clicked', async () => {
    mockUseCardano.mockReset();
    mockUseCardano.mockReturnValue(useCardanoMock_notConnected);
    const history = createMemoryHistory({ initialEntries: [ROUTES.VOTE] });

    const { store } = renderWithProviders(
      <CustomRouter history={history}>
        <VotePage />
      </CustomRouter>,
      { preloadedState: { user: { event: eventMock_active } as UserState } }
    );

    await waitFor(async () => {
      const votePage = await screen.queryByTestId('vote-page');
      const cta = await within(votePage).queryByTestId('proposal-connect-button');

      expect(store.getState().user.isConnectWalletModalVisible).toBeFalsy();
      fireEvent.click(cta);
      expect(store.getState().user.isConnectWalletModalVisible).toEqual(true);
    });
  });

  test('should open connect wallet modal once option selected', async () => {
    mockUseCardano.mockReset();
    mockUseCardano.mockReturnValue(useCardanoMock_notConnected);
    const history = createMemoryHistory({ initialEntries: [ROUTES.VOTE] });

    const { store } = renderWithProviders(
      <CustomRouter history={history}>
        <VotePage />
      </CustomRouter>,
      { preloadedState: { user: { event: eventMock_active } as UserState } }
    );

    await waitFor(async () => {
      const votePage = await screen.queryByTestId('vote-page');
      const options = await within(votePage).queryAllByTestId('option-card');

      expect(store.getState().user.isConnectWalletModalVisible).toBeFalsy();
      fireEvent.click(options[0]);
      expect(store.getState().user.isConnectWalletModalVisible).toEqual(true);
      expect(screen.queryAllByRole('button', { pressed: true })[0].textContent).toEqual(
        eventMock_active.categories[0].proposals[0].presentationName
      );
    });
  });

  test('should display proper state when wallet is connected', async () => {
    const history = createMemoryHistory({ initialEntries: [ROUTES.VOTE] });

    renderWithProviders(
      <CustomRouter history={history}>
        <VotePage />
      </CustomRouter>,
      { preloadedState: { user: { event: eventMock_active } as UserState } }
    );

    await waitFor(async () => {
      const votePage = await screen.queryByTestId('vote-page');
      expect(votePage).not.toBeNull();

      const eventTitle = await within(votePage).queryByTestId('event-title');
      expect(eventTitle).not.toBeNull();
      expect(eventTitle.textContent).toEqual('CIP-1694 Vote');

      const eventTime = await within(votePage).queryByTestId('event-time');
      expect(eventTime).not.toBeNull();
      expect(eventTime.textContent).toEqual(`Voting closes: ${formatUTCDate(eventMock_active.eventEnd.toString())}`);

      const eventDescription = await within(votePage).queryByTestId('event-description');
      expect(eventDescription).not.toBeNull();
      expect(eventDescription.textContent).toEqual('(..)');

      const options = await within(votePage).queryAllByTestId('option-card');
      expect(options.length).toEqual(eventMock_active.categories[0].proposals.length);
      expect(options[0].textContent).toEqual(eventMock_active.categories[0].proposals[0].presentationName);
      expect(options[1].textContent).toEqual(eventMock_active.categories[0].proposals[1].presentationName);
      expect(options[2].textContent).toEqual(eventMock_active.categories[0].proposals[2].presentationName);

      const cta = await within(votePage).queryByTestId('proposal-submit-button');
      expect(cta).not.toBeNull();
      expect(cta.textContent).toEqual('Submit your vote');
    });
  });

  test('should not submit vote if no option selected', async () => {
    const history = createMemoryHistory({ initialEntries: [ROUTES.VOTE] });

    renderWithProviders(
      <CustomRouter history={history}>
        <VotePage />
      </CustomRouter>,
      { preloadedState: { user: { event: eventMock_active } as UserState } }
    );

    await waitFor(async () => {
      const votePage = await screen.queryByTestId('vote-page');
      const cta = await within(votePage).queryByTestId('proposal-submit-button');

      expect(cta.closest('button')).toHaveAttribute('disabled');

      fireEvent.click(cta);
      expect(mockCastAVoteWithDigitalSignature).not.toHaveBeenCalled();
    });
  });

  test('should submit vote', async () => {
    const mockSignMessage = jest.fn().mockImplementation(async (message) => await message);

    mockUseCardano.mockReset();
    mockUseCardano.mockReturnValue({
      ...useCardanoMock,
      signMessage: mockSignMessage,
    });
    mockGetSignedMessagePromise.mockReset();
    mockGetSignedMessagePromise.mockImplementation(
      (signMessage: (message: string) => string) => async (message: string) => await signMessage(message)
    );
    mockGetVotingPower.mockReset();
    mockGetVotingPower.mockResolvedValue(accountDataMock);
    mockBuildCanonicalVoteInputJson.mockReset();
    mockBuildCanonicalVoteInputJson.mockReturnValue(canonicalVoteInputJsonMock);
    const history = createMemoryHistory({ initialEntries: [ROUTES.VOTE] });

    const { store } = renderWithProviders(
      <CustomRouter history={history}>
        <VotePage />
      </CustomRouter>,
      { preloadedState: { user: { event: eventMock_active, isReceiptFetched: true } as UserState } }
    );

    const votePage = await screen.findByTestId('vote-page');
    const options = await within(votePage).queryAllByTestId('option-card');

    fireEvent.click(options[0]);

    const cta = await within(votePage).queryByTestId('proposal-submit-button');
    expect(cta).not.toBeNull();

    expect(store.getState().user.isVoteSubmittedModalVisible).toBeFalsy();
    fireEvent.click(cta);

    await waitFor(() => {
      expect(mockCastAVoteWithDigitalSignature).toHaveBeenCalledWith(canonicalVoteInputJsonMock);
      expect(store.getState().user.isVoteSubmittedModalVisible).toBeTruthy;
    });
  });

  test.skip('should fetch receipt and display proper state if present', async () => {
    const mockSignMessage = jest.fn().mockImplementation(async (message) => await message);
    mockGetVoteReceipt.mockReset();
    mockGetVoteReceipt.mockReturnValue(VoteReceiptMock_Basic);
    mockUseCardano.mockReset();
    mockUseCardano.mockReturnValue({
      ...useCardanoMock,
      signMessage: mockSignMessage,
    });
    mockGetSignedMessagePromise.mockReset();
    mockGetSignedMessagePromise.mockImplementation(
      (signMessage: (message: string) => string) => async (message: string) => await signMessage(message)
    );
    mockGetVotingPower.mockReset();
    mockGetVotingPower.mockResolvedValue(accountDataMock);

    const history = createMemoryHistory({ initialEntries: [ROUTES.VOTE] });
    const { store } = renderWithProviders(
      <CustomRouter history={history}>
        <VotePage />
      </CustomRouter>,
      { preloadedState: { user: { event: eventMock_active } as UserState } }
    );

    await waitFor(async () => {
      const votePage = await screen.findByTestId('vote-page');

      expect(store.getState().user.proposal).toEqual(VoteReceiptMock_Basic.proposal);
      expect(store.getState().user.receipt).toEqual(VoteReceiptMock_Basic);

      expect(screen.queryAllByRole('button', { pressed: true })[0].textContent).toEqual(
        eventMock_active.categories[0].proposals.find(({ name }) => name === VoteReceiptMock_Basic.proposal)
          .presentationName
      );

      const cta = await within(votePage).queryByTestId('show-receipt-button');
      expect(cta).not.toBeNull();
      expect(cta.textContent).toEqual('Vote receipt');
    });
  });

  test.skip('should handle show vote receipt', async () => {
    const mockSignMessage = jest.fn().mockImplementation(async (message) => await message);
    mockGetVoteReceipt.mockReset();
    mockGetVoteReceipt.mockReturnValue(VoteReceiptMock_Basic);
    mockUseCardano.mockReset();
    mockUseCardano.mockReturnValue({
      ...useCardanoMock,
      signMessage: mockSignMessage,
    });
    mockGetSignedMessagePromise.mockReset();
    mockGetSignedMessagePromise.mockImplementation(
      (signMessage: (message: string) => string) => async (message: string) => await signMessage(message)
    );
    mockGetVotingPower.mockReset();
    mockGetVotingPower.mockResolvedValue(accountDataMock);

    const history = createMemoryHistory({ initialEntries: [ROUTES.VOTE] });
    renderWithProviders(
      <CustomRouter history={history}>
        <VotePage />
      </CustomRouter>,
      { preloadedState: { user: { event: eventMock_active } as UserState } }
    );

    const votePage = await screen.findByTestId('vote-page');

    expect(screen.queryByTestId('vote-receipt')).not.toBeInTheDocument();

    const cta = await within(votePage).findByTestId('show-receipt-button');
    fireEvent.click(cta);

    await waitFor(async () => {
      expect(screen.queryByTestId('vote-receipt')).toBeInTheDocument();
    });
  });

  test.skip('should handle error case of refetch receipt functionality', async () => {
    const mockSignMessage = jest.fn().mockImplementation(async (message) => await message);
    mockGetVoteReceipt.mockReset();
    mockGetVoteReceipt.mockReturnValue(VoteReceiptMock_Full_MediumAssurance);
    mockUseCardano.mockReset();
    mockUseCardano.mockReturnValue({
      ...useCardanoMock,
      signMessage: mockSignMessage,
    });
    mockGetSignedMessagePromise.mockReset();
    mockGetSignedMessagePromise.mockImplementation(
      (signMessage: (message: string) => string) => async (message: string) => await signMessage(message)
    );
    mockGetVotingPower.mockReset();
    mockGetVotingPower.mockResolvedValue(accountDataMock);

    const history = createMemoryHistory({ initialEntries: [ROUTES.VOTE] });
    renderWithProviders(
      <CustomRouter history={history}>
        <VotePage />
      </CustomRouter>,
      { preloadedState: { user: { event: eventMock_active } as UserState } }
    );

    const votePage = await screen.findByTestId('vote-page');

    expect(screen.queryByTestId('vote-receipt')).not.toBeInTheDocument();

    const cta = await within(votePage).findByTestId('show-receipt-button');
    fireEvent.click(cta);

    const receipt = await screen.findByTestId('vote-receipt');
    expect(receipt).toBeInTheDocument();

    mockGetVoteReceipt.mockReset();
    mockGetVoteReceipt.mockImplementation(async () => await Promise.reject('error'));

    fireEvent.click(await within(receipt).findByTestId('refetch-receipt-button'));

    await waitFor(async () => {
      expect(mockToast).toBeCalledWith(
        <Toast
          message="Unable to refresh your vote receipt. Please try again"
          error
          icon={<BlockIcon style={{ fontSize: '19px', color: '#F5F9FF' }} />}
        />
      );
    });
  });
});

describe("For the event that hasn't started yet", () => {
  beforeEach(() => {
    mockUseCardano.mockReturnValue(useCardanoMock);
    mockGetSlotNumber.mockReturnValue(chainTipMock);
    mockGetVoteReceipt.mockReturnValue(null);
  });
  afterEach(() => {
    jest.clearAllMocks();
    cleanup();
  });

  test('should display proper state', async () => {
    mockUseCardano.mockReset();
    mockUseCardano.mockReturnValue(useCardanoMock_notConnected);

    const history = createMemoryHistory({ initialEntries: [ROUTES.VOTE] });

    renderWithProviders(
      <CustomRouter history={history}>
        <VotePage />
      </CustomRouter>,
      { preloadedState: { user: { event: eventMock_notStarted } as UserState } }
    );

    await waitFor(async () => {
      const votePage = await screen.queryByTestId('vote-page');
      expect(votePage).not.toBeNull();

      const eventTitle = await within(votePage).queryByTestId('event-title');
      expect(eventTitle).not.toBeNull();
      expect(eventTitle.textContent).toEqual('CIP-1694 Vote');

      const eventTime = await within(votePage).queryByTestId('event-time');
      expect(eventTime).not.toBeNull();
      expect(eventTime.textContent).toEqual(
        `Vote from: ${formatUTCDate(eventMock_notStarted.eventStart.toString())} - ${formatUTCDate(
          eventMock_notStarted.eventEnd.toString()
        )}`
      );

      const eventDescription = await within(votePage).queryByTestId('event-description');
      expect(eventDescription).not.toBeNull();
      expect(eventDescription.textContent).toEqual('(..)');

      const options = await within(votePage).queryAllByTestId('option-card');
      expect(options.length).toEqual(eventMock_notStarted.categories[0].proposals.length);
      expect(options[0].textContent).toEqual(eventMock_notStarted.categories[0].proposals[0].presentationName);
      expect(options[0].closest('button')).toHaveAttribute('disabled');
      expect(options[1].textContent).toEqual(eventMock_notStarted.categories[0].proposals[1].presentationName);
      expect(options[1].closest('button')).toHaveAttribute('disabled');
      expect(options[2].textContent).toEqual(eventMock_notStarted.categories[0].proposals[2].presentationName);
      expect(options[2].closest('button')).toHaveAttribute('disabled');

      const cta = await within(votePage).queryByTestId('event-hasnt-started-submit-button');
      expect(cta).not.toBeNull();
      expect(cta.textContent).toEqual(
        `Submit your vote from ${getDateAndMonth(eventMock_notStarted.eventStart?.toString())}`
      );
    });
  });
});

describe('For the event that has already finished', () => {
  beforeEach(() => {
    mockUseCardano.mockReturnValue(useCardanoMock);
    mockGetSlotNumber.mockReturnValue(chainTipMock);
    mockGetVoteReceipt.mockReturnValue({});
  });
  afterEach(() => {
    jest.clearAllMocks();
    cleanup();
  });

  test('should display proper state', async () => {
    mockUseCardano.mockReset();
    mockUseCardano.mockReturnValue(useCardanoMock_notConnected);

    const history = createMemoryHistory({ initialEntries: [ROUTES.VOTE] });

    renderWithProviders(
      <CustomRouter history={history}>
        <VotePage />
      </CustomRouter>,
      { preloadedState: { user: { event: eventMock_finished } as UserState } }
    );

    await waitFor(async () => {
      const votePage = await screen.queryByTestId('vote-page');
      expect(votePage).not.toBeNull();

      const eventTitle = await within(votePage).queryByTestId('event-title');
      expect(eventTitle).not.toBeNull();
      expect(eventTitle.textContent).toEqual('CIP-1694 Vote');

      const eventTime = await within(votePage).queryByTestId('event-time');
      expect(eventTime).not.toBeNull();
      expect(eventTime.textContent).toEqual(
        `The vote closed on ${formatUTCDate(eventMock_finished.eventEnd.toString())}`
      );

      const eventDescription = await within(votePage).queryByTestId('event-description');
      expect(eventDescription).not.toBeNull();
      expect(eventDescription.textContent).toEqual('(..)');

      const options = await within(votePage).queryAllByTestId('option-card');
      expect(options.length).toEqual(eventMock_finished.categories[0].proposals.length);
      expect(options[0].textContent).toEqual(eventMock_finished.categories[0].proposals[0].presentationName);
      expect(options[1].textContent).toEqual(eventMock_finished.categories[0].proposals[1].presentationName);
      expect(options[2].textContent).toEqual(eventMock_finished.categories[0].proposals[2].presentationName);

      const cta = await within(votePage).queryByTestId('proposal-connect-button');
      expect(cta).not.toBeNull();
      expect(cta.textContent).toEqual('Connect wallet to see your vote');
    });
  });

  test('should render connect wallet button and open connect wallet modal once clicked', async () => {
    mockUseCardano.mockReset();
    mockUseCardano.mockReturnValue(useCardanoMock_notConnected);
    const history = createMemoryHistory({ initialEntries: [ROUTES.VOTE] });

    const { store } = renderWithProviders(
      <CustomRouter history={history}>
        <VotePage />
      </CustomRouter>,
      { preloadedState: { user: { event: eventMock_finished } as UserState } }
    );

    await waitFor(async () => {
      const votePage = await screen.queryByTestId('vote-page');
      const cta = await within(votePage).queryByTestId('proposal-connect-button');

      expect(store.getState().user.isConnectWalletModalVisible).toBeFalsy();
      fireEvent.click(cta);
      expect(store.getState().user.isConnectWalletModalVisible).toEqual(true);
    });
  });

  test('should not open connect wallet modal once option selected', async () => {
    const history = createMemoryHistory({ initialEntries: [ROUTES.VOTE] });

    const { store } = renderWithProviders(
      <CustomRouter history={history}>
        <VotePage />
      </CustomRouter>,
      { preloadedState: { user: { event: eventMock_finished, isReceiptFetched: true } as UserState } }
    );

    const votePage = await screen.findByTestId('vote-page');
    const options = await within(votePage).queryAllByTestId('option-card');

    expect(store.getState().user.isConnectWalletModalVisible).toBeFalsy();
    fireEvent.click(options[0]);

    await waitFor(async () => {
      expect(store.getState().user.isConnectWalletModalVisible).toBeFalsy();
      expect(screen.queryAllByRole('button', { pressed: true }).length).toEqual(0);
    });
  });

  test.skip('should render view result and open receipt cta when wallet is connected', async () => {
    const mockSignMessage = jest.fn().mockImplementation(async (message) => await message);
    mockGetVoteReceipt.mockReset();
    mockGetVoteReceipt.mockReturnValue(VoteReceiptMock_Basic);
    mockUseCardano.mockReset();
    mockUseCardano.mockReturnValue({
      ...useCardanoMock,
      signMessage: mockSignMessage,
    });
    mockGetSignedMessagePromise.mockReset();
    mockGetSignedMessagePromise.mockImplementation(
      (signMessage: (message: string) => string) => async (message: string) => await signMessage(message)
    );
    mockGetVotingPower.mockReset();
    mockGetVotingPower.mockResolvedValue(accountDataMock);
    const history = createMemoryHistory({ initialEntries: [ROUTES.VOTE] });
    const historyPushSpy = jest.spyOn(history, 'push');

    renderWithProviders(
      <CustomRouter history={history}>
        <VotePage />
      </CustomRouter>,
      { preloadedState: { user: { event: eventMock_finished } as UserState } }
    );

    const votePage = await screen.findByTestId('vote-page');

    expect(screen.queryByTestId('vote-receipt')).not.toBeInTheDocument();

    const cta = await within(votePage).findByTestId('show-receipt-button');
    fireEvent.click(cta);

    await waitFor(async () => {
      expect(screen.queryByTestId('vote-receipt')).toBeInTheDocument();
    });
    const closeVoteReceipt = await within(screen.queryByTestId('vote-receipt')).findByTestId(
      'vote-receipt-close-button'
    );
    fireEvent.click(closeVoteReceipt);

    await waitFor(async () => {
      expect(screen.queryByTestId('vote-receipt')).not.toBeInTheDocument();
    });

    const cta2 = await within(votePage).findByTestId('view-results-button');
    fireEvent.click(cta2);

    await waitFor(async () => {
      expect((historyPushSpy.mock.lastCall[0] as unknown as any).pathname).toEqual(ROUTES.LEADERBOARD);
    });
  });
});
