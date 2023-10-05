/* eslint-disable @typescript-eslint/no-empty-function */
/* eslint-disable @typescript-eslint/no-explicit-any */
/* eslint-disable no-var */
var mockUseCardano = jest.fn();
var mockCastAVoteWithDigitalSignature = jest.fn();
var mockGetVotingPower = jest.fn();
var mockBuildCanonicalVoteInputJson = jest.fn();
var mockGetSignedMessagePromise = jest.fn();
var mockGetChainTip = jest.fn();
var mockGetVoteReceipt = jest.fn();
var mockToast = jest.fn();
var mockGetUserInSession = jest.fn();
var mockSaveUserInSession = jest.fn();
var mockTokenIsExpired = jest.fn();
var submitLoginMock = jest.fn();
var buildCanonicalLoginJsonMock = jest.fn();
/* eslint-disable import/imports-first */
import 'whatwg-fetch';
import '@testing-library/jest-dom';
import { expect } from '@jest/globals';
import { screen, within, waitFor, cleanup, fireEvent, act, waitForElementToBeRemoved } from '@testing-library/react';
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
  userInSessionMock,
  // VoteReceiptMock_Full_MediumAssurance,
} from 'test/mocks';
import { CustomRouter } from 'test/CustomRouter';
import { capitalize } from 'lodash';
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
      CATEGORY_ID: 'CHANGE_GOV_STRUCTURE',
      EVENT_ID: 'CIP-1694_Pre_Ratification_3316',
    },
  };
});

jest.mock('common/api/voteService', () => ({
  ...jest.requireActual('common/api/voteService'),
  castAVoteWithDigitalSignature: mockCastAVoteWithDigitalSignature,
  getVotingPower: mockGetVotingPower,
  getChainTip: mockGetChainTip,
  getVoteReceipt: mockGetVoteReceipt,
}));

jest.mock('common/api/loginService', () => ({
  ...jest.requireActual('common/api/loginService'),
  submitLogin: submitLoginMock,
  buildCanonicalLoginJson: buildCanonicalLoginJsonMock,
}));

jest.mock('common/utils/voteUtils', () => ({
  ...jest.requireActual('common/utils/voteUtils'),
  buildCanonicalVoteInputJson: mockBuildCanonicalVoteInputJson,
  getSignedMessagePromise: mockGetSignedMessagePromise,
}));

jest.mock('common/utils/session', () => ({
  ...jest.requireActual('common/utils/session'),
  getUserInSession: mockGetUserInSession,
  saveUserInSession: mockSaveUserInSession,
  tokenIsExpired: mockTokenIsExpired,
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
    mockGetChainTip.mockReturnValue(chainTipMock);
    mockGetUserInSession.mockReturnValue({ accessToken: true });
    mockTokenIsExpired.mockReturnValue(false);
    mockGetVoteReceipt.mockReturnValue({});
  });
  afterEach(() => {
    jest.clearAllMocks();
    cleanup();
  });

  test('should display proper state when the event related data is loading', async () => {
    const history = createMemoryHistory({ initialEntries: [ROUTES.VOTE] });

    renderWithProviders(
      <CustomRouter history={history}>
        <VotePage />
      </CustomRouter>
    );

    await waitFor(async () => {
      const votePage = screen.queryByTestId('vote-page');

      const optionsLoaders = within(votePage).queryAllByTestId('option-card-loader');
      expect(optionsLoaders.length).toEqual(3);
    });
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
      const votePage = screen.queryByTestId('vote-page');
      expect(votePage).not.toBeNull();

      const eventTitle = within(votePage).queryByTestId('event-title');
      expect(eventTitle).not.toBeNull();
      expect(eventTitle.textContent).toEqual('The Governance of Cardano');

      const eventTime = within(votePage).queryByTestId('event-time');
      expect(eventTime).not.toBeNull();
      expect(eventTime.textContent).toEqual(
        `Voting closes: ${formatUTCDate(eventMock_active.eventEndDate.toString())}`
      );

      const eventDescription = within(votePage).queryByTestId('event-description');
      expect(eventDescription).not.toBeNull();
      expect(eventDescription.textContent).toEqual('Do you like pineapple pizza?');

      const options = within(votePage).queryAllByTestId('option-card');
      expect(options.length).toEqual(eventMock_active.categories[0].proposals.length);
      for (const option in options) {
        expect(options[option].textContent).toEqual(
          capitalize(eventMock_active.categories[0].proposals[option].name.toLowerCase())
        );
      }

      const cta = within(votePage).queryByTestId('proposal-connect-button');
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
      const votePage = screen.queryByTestId('vote-page');
      const cta = within(votePage).queryByTestId('proposal-connect-button');

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
      const votePage = screen.queryByTestId('vote-page');
      const options = within(votePage).queryAllByTestId('option-card');

      expect(store.getState().user.isConnectWalletModalVisible).toBeFalsy();
      fireEvent.click(options[0]);
      expect(store.getState().user.isConnectWalletModalVisible).toEqual(true);
      expect(screen.queryAllByRole('button', { pressed: true })[0].textContent).toEqual(
        capitalize(eventMock_active.categories[0].proposals[0].name.toLowerCase())
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
      const votePage = screen.queryByTestId('vote-page');
      expect(votePage).not.toBeNull();

      const eventTitle = within(votePage).queryByTestId('event-title');
      expect(eventTitle).not.toBeNull();
      expect(eventTitle.textContent).toEqual('The Governance of Cardano');

      const eventTime = within(votePage).queryByTestId('event-time');
      expect(eventTime).not.toBeNull();
      expect(eventTime.textContent).toEqual(
        `Voting closes: ${formatUTCDate(eventMock_active.eventEndDate.toString())}`
      );

      const eventDescription = within(votePage).queryByTestId('event-description');
      expect(eventDescription).not.toBeNull();
      expect(eventDescription.textContent).toEqual('Do you like pineapple pizza?');

      const options = within(votePage).queryAllByTestId('option-card');
      expect(options.length).toEqual(eventMock_active.categories[0].proposals.length);
      for (const option in options) {
        expect(options[option].textContent).toEqual(
          capitalize(eventMock_active.categories[0].proposals[option].name.toLowerCase())
        );
      }

      const cta = within(votePage).queryByTestId('proposal-submit-button');
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
      const votePage = screen.queryByTestId('vote-page');
      const cta = within(votePage).queryByTestId('proposal-submit-button');

      expect(cta.closest('button')).toBeDisabled();

      fireEvent.click(cta);
      expect(mockCastAVoteWithDigitalSignature).not.toHaveBeenCalled();
    });
  });

  test('should handle error during submit vote', async () => {
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

    const errorMessage = 'error';
    const error = new Error(errorMessage);
    mockCastAVoteWithDigitalSignature.mockReset();
    mockCastAVoteWithDigitalSignature.mockImplementation(async () => await Promise.reject(error));

    const history = createMemoryHistory({ initialEntries: [ROUTES.VOTE] });

    await act(async () => {
      renderWithProviders(
        <CustomRouter history={history}>
          <VotePage />
        </CustomRouter>,
        { preloadedState: { user: { event: eventMock_active, tip: chainTipMock } as UserState } }
      );
    });

    const votePage = screen.queryByTestId('vote-page');

    const options = within(votePage).queryAllByTestId('option-card');

    fireEvent.click(options[0]);

    const cta = within(votePage).queryByTestId('proposal-submit-button');
    await act(async () => {
      fireEvent.click(cta);
    });

    expect(mockToast).toBeCalledWith(
      <Toast
        message={'Unable to submit your vote. Please try again'}
        error
        icon={<BlockIcon style={{ fontSize: '19px', color: '#F5F9FF' }} />}
      />
    );
    mockCastAVoteWithDigitalSignature.mockReset();
  });

  test('should submit vote and fetch vote receipt if there are more that on category', async () => {
    const accessToken = 'accessToken';
    mockGetUserInSession.mockReset();
    mockGetUserInSession.mockReturnValue({ accessToken });
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

    let store: ReturnType<typeof renderWithProviders>['store'];
    await act(async () => {
      ({ store } = renderWithProviders(
        <CustomRouter history={history}>
          <VotePage />
        </CustomRouter>,
        { preloadedState: { user: { event: eventMock_active, tip: chainTipMock } as UserState } }
      ));
    });

    const votePage = screen.queryByTestId('vote-page');

    const options = within(votePage).queryAllByTestId('option-card');

    await act(async () => {
      fireEvent.click(options[0]);
    });

    const cta = within(votePage).queryByTestId('proposal-submit-button');
    expect(cta).not.toBeNull();
    expect(cta.closest('button')).not.toBeDisabled();
    expect(mockGetVoteReceipt.mock.calls[0]).toEqual([eventMock_active.categories[0].id, accessToken]);

    expect(store.getState().user.isVoteSubmittedModalVisible).toBeFalsy();
    await act(async () => {
      fireEvent.click(cta);
    });

    expect(mockCastAVoteWithDigitalSignature).toHaveBeenCalledWith(canonicalVoteInputJsonMock);
    expect(store.getState().user.isVoteSubmittedModalVisible).toBeTruthy;

    await act(async () => {
      fireEvent.click(screen.queryByTestId('vote-submitted-close'));
    });

    expect(store.getState().user.isVoteSubmittedModalVisible).toBeFalsy;
    expect(mockGetVoteReceipt).toBeCalledTimes(2);
    expect(mockGetVoteReceipt.mock.calls[1]).toEqual([eventMock_active.categories[1].id, accessToken]);
    expect(screen.queryAllByRole('button', { pressed: true }).length).toEqual(0);
    expect(within(votePage).queryByTestId('next-question-button')).toBeNull();
  });

  test('should submit vote and fetch receipt for single category', async () => {
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

    await act(async () => {
      renderWithProviders(
        <CustomRouter history={history}>
          <VotePage />
        </CustomRouter>,
        {
          preloadedState: {
            user: {
              event: {
                ...eventMock_active,
                categories: [eventMock_active.categories[0]],
              },
              tip: chainTipMock,
            } as UserState,
          },
        }
      );
    });

    const votePage = screen.queryByTestId('vote-page');

    const options = within(votePage).queryAllByTestId('option-card');

    await act(async () => {
      fireEvent.click(options[0]);
    });
    const cta = within(votePage).queryByTestId('proposal-submit-button');
    await act(async () => {
      fireEvent.click(cta);
    });

    expect(mockGetVoteReceipt).toBeCalledTimes(2);
  });

  test('should submit vote, fetch receipt for the last category and stay on the last category', async () => {
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
    mockGetVoteReceipt.mockReset();
    mockGetVoteReceipt.mockReturnValue(VoteReceiptMock_Basic);

    const history = createMemoryHistory({ initialEntries: [ROUTES.VOTE] });

    await act(async () => {
      renderWithProviders(
        <CustomRouter history={history}>
          <VotePage />
        </CustomRouter>,
        {
          preloadedState: {
            user: {
              event: eventMock_active,
              tip: chainTipMock,
            } as UserState,
          },
        }
      );
    });

    const votePage = screen.queryByTestId('vote-page');
    await act(async () => {
      const error = { message: 'VOTE_NOT_FOUND' };
      mockGetVoteReceipt.mockReset();
      mockGetVoteReceipt.mockImplementation(async () => await Promise.reject(error));
      fireEvent.click(within(votePage).queryByText('Next question'));
    });

    expect(mockGetVoteReceipt).toBeCalledTimes(1);
    expect(mockGetVoteReceipt).toHaveBeenLastCalledWith(eventMock_active.categories[1].id, true);
    expect(within(votePage).queryByTestId('next-question-button')).not.toBeInTheDocument();

    const options = within(votePage).queryAllByTestId('option-card');
    await act(async () => {
      fireEvent.click(options[0]);
    });

    await act(async () => {
      fireEvent.click(within(votePage).queryByTestId('proposal-submit-button'));
    });

    expect(mockGetVoteReceipt).toBeCalledTimes(2);
    expect(mockGetVoteReceipt).toHaveBeenLastCalledWith(eventMock_active.categories[1].id, true);
    expect(within(votePage).queryByText('Previous question')).not.toBeInTheDocument();
    expect(within(votePage).queryByTestId('show-receipt-button')).toBeInTheDocument();
  });

  test('should show proper error if failed to fetch voting power during vote submitting', async () => {
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
    const error = { message: 'error' };
    mockGetVotingPower.mockReset();
    mockGetVotingPower.mockImplementation(async () => await Promise.reject(error));
    mockBuildCanonicalVoteInputJson.mockReset();
    mockBuildCanonicalVoteInputJson.mockReturnValue(canonicalVoteInputJsonMock);

    const history = createMemoryHistory({ initialEntries: [ROUTES.VOTE] });

    await act(async () => {
      renderWithProviders(
        <CustomRouter history={history}>
          <VotePage />
        </CustomRouter>,
        { preloadedState: { user: { event: eventMock_active, tip: chainTipMock } as UserState } }
      );
    });

    const votePage = screen.queryByTestId('vote-page');

    const options = within(votePage).queryAllByTestId('option-card');

    await act(async () => {
      fireEvent.click(options[0]);
    });
    const cta = within(votePage).queryByTestId('proposal-submit-button');

    await act(async () => {
      fireEvent.click(cta);
    });
    expect(mockToast).toBeCalledWith(
      <Toast
        message={'Unable to submit your vote. Please try again'}
        error
        icon={<BlockIcon style={{ fontSize: '19px', color: '#F5F9FF' }} />}
      />
    );
  });

  test('should ask to fetch receipt and display proper state if present and user session is active', async () => {
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

    mockGetUserInSession.mockReset();
    mockGetUserInSession.mockReturnValue({ accessToken: true });
    mockTokenIsExpired.mockReset();
    mockTokenIsExpired.mockReturnValue(false);

    const history = createMemoryHistory({ initialEntries: [ROUTES.VOTE] });
    renderWithProviders(
      <CustomRouter history={history}>
        <VotePage />
      </CustomRouter>,
      { preloadedState: { user: { event: eventMock_active, tip: chainTipMock } as UserState } }
    );

    await waitFor(async () => {
      const confirmationModal = screen.queryByTestId('confirm-with-signature-modal');
      expect(confirmationModal).toBeNull();
    });
  });

  test('should show proper message if user login servce throws', async () => {
    const canonicalVoteInput = 'canonicalVoteInput';
    buildCanonicalLoginJsonMock.mockReset();
    buildCanonicalLoginJsonMock.mockReturnValue(canonicalVoteInput);
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

    mockGetUserInSession.mockReset();
    mockGetUserInSession.mockReturnValue(null);

    const error = { message: 'error' };
    submitLoginMock.mockReset();
    submitLoginMock.mockImplementation(async () => await Promise.reject(error));

    const history = createMemoryHistory({ initialEntries: [ROUTES.VOTE] });
    renderWithProviders(
      <CustomRouter history={history}>
        <VotePage />
      </CustomRouter>,
      { preloadedState: { user: { event: eventMock_active, tip: chainTipMock } as UserState } }
    );

    const confirmationModal = await screen.findByTestId('confirm-with-signature-modal');
    const confirmCta = await within(confirmationModal).findByTestId('confirm-with-signature-cta');
    expect(confirmCta).toHaveTextContent('Confirm');

    await act(async () => {
      fireEvent.click(confirmCta);
    });
    await waitFor(async () => {
      expect(mockToast).toBeCalledWith(
        <Toast
          message={error.message}
          error
          icon={<BlockIcon style={{ fontSize: '19px', color: '#F5F9FF' }} />}
        />
      );
    });
  });

  test('should ask to fetch receipt and display proper state if present and there is no user session', async () => {
    const canonicalVoteInput = 'canonicalVoteInput';
    buildCanonicalLoginJsonMock.mockReset();
    buildCanonicalLoginJsonMock.mockReturnValue(canonicalVoteInput);
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

    mockGetUserInSession.mockReset();
    mockGetUserInSession.mockReturnValue(null);

    submitLoginMock.mockReset();
    submitLoginMock.mockReturnValue(userInSessionMock);

    const history = createMemoryHistory({ initialEntries: [ROUTES.VOTE] });
    renderWithProviders(
      <CustomRouter history={history}>
        <VotePage />
      </CustomRouter>,
      { preloadedState: { user: { event: eventMock_active, tip: chainTipMock } as UserState } }
    );

    const votePage = await screen.findByTestId('vote-page');

    const confirmationModal = await screen.findByTestId('confirm-with-signature-modal');
    expect(confirmationModal).not.toBeNull();
    expect(await within(confirmationModal).findByTestId('confirm-with-signature-title')).toHaveTextContent(
      'Wallet signature'
    );
    expect(await within(confirmationModal).findByTestId('confirm-with-signature-description')).toHaveTextContent(
      'We need to check if you’ve already voted. Please confirm with your wallet signature.'
    );
    const confirmCta = await within(confirmationModal).findByTestId('confirm-with-signature-cta');
    expect(confirmCta).toHaveTextContent('Confirm');

    fireEvent.click(confirmCta);

    await waitFor(async () => {
      expect(screen.queryAllByRole('button', { pressed: true })[0].textContent).toEqual(
        capitalize(
          eventMock_active.categories[0].proposals
            .find(({ name }) => name === VoteReceiptMock_Basic.proposal)
            .name.toLowerCase()
        )
      );

      const cta = within(votePage).queryByTestId('show-receipt-button');
      expect(cta).not.toBeNull();
      expect(cta.textContent).toEqual('Vote receipt');

      expect(screen.queryByTestId('confirm-with-signature-modal')).toBeNull();
      expect(submitLoginMock).toBeCalledWith(canonicalVoteInput);
      expect(mockSaveUserInSession).toBeCalledWith(userInSessionMock);
    });
  });

  test('should ask to fetch receipt and display proper state if present and user token has expired', async () => {
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

    mockGetUserInSession.mockReset();
    mockGetUserInSession.mockReturnValue({ accessToken: true });

    mockTokenIsExpired.mockReset();
    mockTokenIsExpired.mockReturnValue(true);

    submitLoginMock.mockReset();
    submitLoginMock.mockReturnValue(userInSessionMock);

    const history = createMemoryHistory({ initialEntries: [ROUTES.VOTE] });
    renderWithProviders(
      <CustomRouter history={history}>
        <VotePage />
      </CustomRouter>,
      { preloadedState: { user: { event: eventMock_active, tip: chainTipMock } as UserState } }
    );

    const votePage = await screen.findByTestId('vote-page');

    const confirmationModal = await screen.findByTestId('confirm-with-signature-modal');
    expect(confirmationModal).not.toBeNull();
    expect(await within(confirmationModal).findByTestId('confirm-with-signature-title')).toHaveTextContent(
      'Wallet signature'
    );
    expect(await within(confirmationModal).findByTestId('confirm-with-signature-description')).toHaveTextContent(
      'We need to check if you’ve already voted. Please confirm with your wallet signature.'
    );
    const confirmCta = await within(confirmationModal).findByTestId('confirm-with-signature-cta');
    expect(confirmCta).toHaveTextContent('Confirm');

    fireEvent.click(confirmCta);

    await waitFor(async () => {
      expect(screen.queryAllByRole('button', { pressed: true })[0].textContent).toEqual(
        capitalize(
          eventMock_active.categories[0].proposals
            .find(({ name }) => name === VoteReceiptMock_Basic.proposal)
            .name.toLowerCase()
        )
      );

      const cta = within(votePage).queryByTestId('show-receipt-button');
      expect(cta).not.toBeNull();
      expect(cta.textContent).toEqual('Vote receipt');

      expect(screen.queryByTestId('confirm-with-signature-modal')).toBeNull();
    });
  });

  test('should show proper error if vote is not found', async () => {
    const mockSignMessage = jest.fn().mockImplementation(async (message) => await message);
    const error = { message: 'VOTE_NOT_FOUND' };
    mockGetVoteReceipt.mockReset();
    mockGetVoteReceipt.mockImplementation(async () => await Promise.reject(error));

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

    mockGetUserInSession.mockReset();
    mockGetUserInSession.mockReturnValue({ accessToken: true });
    mockTokenIsExpired.mockReset();
    mockTokenIsExpired.mockReturnValue(false);

    const history = createMemoryHistory({ initialEntries: [ROUTES.VOTE] });
    await act(async () => {
      renderWithProviders(
        <CustomRouter history={history}>
          <VotePage />
        </CustomRouter>,
        {
          preloadedState: {
            user: {
              event: eventMock_active,
              tip: chainTipMock,
            } as UserState,
          },
        }
      );
    });

    const options = screen.queryAllByTestId('option-card');
    fireEvent.click(options[0]);
    expect(screen.queryByTestId('proposal-submit-button').closest('button')).not.toBeDisabled();
  });

  test('should handle show vote receipt', async () => {
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
    await act(async () => {
      renderWithProviders(
        <CustomRouter history={history}>
          <VotePage />
        </CustomRouter>,
        {
          preloadedState: {
            user: {
              event: eventMock_active,
              tip: chainTipMock,
            } as UserState,
          },
        }
      );
    });

    const votePage = await screen.findByTestId('vote-page');
    expect(votePage).toBeInTheDocument();
    expect(screen.queryByTestId('confirm-with-signature-modal')).not.toBeInTheDocument();

    const cta = within(votePage).queryByTestId('show-receipt-button');
    expect(cta.closest('button')).not.toBeDisabled();
    expect(screen.queryByTestId('vote-receipt')).not.toBeInTheDocument();

    await act(async () => {
      fireEvent.click(cta);
    });
    expect(screen.queryByTestId('vote-receipt')).toBeInTheDocument();
  });

  test('should switch between categories and show pagination', async () => {
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
    await act(async () => {
      renderWithProviders(
        <CustomRouter history={history}>
          <VotePage />
        </CustomRouter>,
        {
          preloadedState: {
            user: {
              event: eventMock_active,
              tip: chainTipMock,
            } as UserState,
          },
        }
      );
    });

    const votePage = await screen.findByTestId('vote-page');
    expect(votePage).toBeInTheDocument();

    const cta = within(votePage).queryByTestId('next-question-button');
    expect(cta.closest('button')).not.toBeDisabled();
    expect(mockGetVoteReceipt).toHaveBeenLastCalledWith(eventMock_active.categories[0].id, true);
    expect(cta).toHaveTextContent('Next question');
    expect(within(votePage).queryByTestId('category-pagination')).toHaveTextContent('Question 1 of 2');

    await act(async () => {
      mockGetVoteReceipt.mockReset();
      mockGetVoteReceipt.mockReturnValue({
        ...VoteReceiptMock_Basic,
        category: eventMock_active.categories[1].id,
      });
      fireEvent.click(cta);
    });

    const eventTitle = within(votePage).queryByTestId('event-title');
    expect(eventTitle).not.toBeNull();
    expect(eventTitle.textContent).toEqual('The Governance of Cardano');

    const eventTime = within(votePage).queryByTestId('event-time');
    expect(eventTime).not.toBeNull();
    expect(eventTime.textContent).toEqual(`Voting closes: ${formatUTCDate(eventMock_active.eventEndDate.toString())}`);

    const eventDescription = within(votePage).queryByTestId('event-description');
    expect(eventDescription).not.toBeNull();
    expect(eventDescription.textContent).toEqual('Do you like apples?');

    const options = within(votePage).queryAllByTestId('option-card');
    expect(options.length).toEqual(eventMock_active.categories[1].proposals.length);
    for (const option in options) {
      expect(options[option].textContent).toEqual(
        capitalize(eventMock_active.categories[1].proposals[option].name.toLowerCase())
      );
    }
    expect(screen.queryByTestId('vote-receipt')).not.toBeInTheDocument();
    expect(mockGetVoteReceipt).toHaveBeenLastCalledWith(eventMock_active.categories[1].id, true);
    expect(within(votePage).queryByTestId('next-question-button')).toHaveTextContent('Previous question');
    expect(within(votePage).queryByTestId('category-pagination')).toHaveTextContent('Question 2 of 2');
  });

  test('should handle show vote receipt for inactive user session', async () => {
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
    await act(async () => {
      renderWithProviders(
        <CustomRouter history={history}>
          <VotePage />
        </CustomRouter>,
        {
          preloadedState: {
            user: {
              event: eventMock_active,
              tip: chainTipMock,
            } as UserState,
          },
        }
      );
    });

    const votePage = await screen.findByTestId('vote-page');
    expect(votePage).toBeInTheDocument();
    expect(screen.queryByTestId('confirm-with-signature-modal')).not.toBeInTheDocument();

    const cta = within(votePage).queryByTestId('show-receipt-button');
    expect(cta.closest('button')).not.toBeDisabled();
    expect(screen.queryByTestId('vote-receipt')).not.toBeInTheDocument();

    await act(async () => {
      fireEvent.click(cta);
    });
    expect(screen.queryByTestId('vote-receipt')).toBeInTheDocument();
  });

  test('should handle error case of refetch receipt functionality', async () => {
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

    mockGetUserInSession.mockReset();
    mockGetUserInSession.mockReturnValue({ accessToken: true });
    mockTokenIsExpired.mockReset();
    mockTokenIsExpired.mockReturnValue(false);

    const history = createMemoryHistory({ initialEntries: [ROUTES.VOTE] });
    await act(async () => {
      renderWithProviders(
        <CustomRouter history={history}>
          <VotePage />
        </CustomRouter>,
        {
          preloadedState: {
            user: {
              event: eventMock_active,
              tip: chainTipMock,
            } as UserState,
          },
        }
      );
    });

    const votePage = screen.queryByTestId('vote-page');
    expect(screen.queryByTestId('vote-receipt')).not.toBeInTheDocument();
    const cta = within(votePage).queryByTestId('show-receipt-button');
    await act(async () => fireEvent.click(cta));

    const receipt = await screen.findByTestId('vote-receipt');
    expect(receipt).toBeInTheDocument();

    mockGetVoteReceipt.mockReset();
    mockGetVoteReceipt.mockImplementation(async () => await Promise.reject('error'));

    await act(async () => fireEvent.click(within(receipt).queryByTestId('refetch-receipt-button')));
    expect(mockToast).toBeCalledWith(
      <Toast
        message="Unable to refresh your vote receipt. Please try again"
        error
        icon={<BlockIcon style={{ fontSize: '19px', color: '#F5F9FF' }} />}
      />
    );
  });

  test('should handle refetch receipt functionality', async () => {
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

    mockGetUserInSession.mockReset();
    mockGetUserInSession.mockReturnValue({ accessToken: true });
    mockTokenIsExpired.mockReset();
    mockTokenIsExpired.mockReturnValue(false);

    const history = createMemoryHistory({ initialEntries: [ROUTES.VOTE] });
    await act(async () => {
      renderWithProviders(
        <CustomRouter history={history}>
          <VotePage />
        </CustomRouter>,
        {
          preloadedState: {
            user: {
              event: eventMock_active,
              tip: chainTipMock,
            } as UserState,
          },
        }
      );
    });

    const votePage = screen.queryByTestId('vote-page');
    expect(screen.queryByTestId('vote-receipt')).not.toBeInTheDocument();
    const cta = within(votePage).queryByTestId('show-receipt-button');
    await act(async () => fireEvent.click(cta));

    const receipt = await screen.findByTestId('vote-receipt');

    await act(async () => fireEvent.click(within(receipt).queryByTestId('refetch-receipt-button')));
    expect(mockToast).toBeCalledWith(<Toast message="Receipt has been successfully refreshed" />);
  });
});

describe("For the event that hasn't started yet", () => {
  beforeEach(() => {
    mockUseCardano.mockReturnValue(useCardanoMock);
    mockGetChainTip.mockReturnValue(chainTipMock);
    mockGetVoteReceipt.mockReturnValue(null);
    mockGetUserInSession.mockReturnValue({ accessToken: true });
    mockTokenIsExpired.mockReturnValue(false);
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
      { preloadedState: { user: { event: eventMock_notStarted, tip: chainTipMock } as UserState } }
    );

    await waitFor(async () => {
      const votePage = screen.queryByTestId('vote-page');
      expect(votePage).not.toBeNull();

      const eventTitle = within(votePage).queryByTestId('event-title');
      expect(eventTitle).not.toBeNull();
      expect(eventTitle.textContent).toEqual('The Governance of Cardano');

      const eventTime = within(votePage).queryByTestId('event-time');
      expect(eventTime).not.toBeNull();
      expect(eventTime.textContent).toEqual(
        `Vote from: ${formatUTCDate(eventMock_notStarted.eventStartDate.toString())} - ${formatUTCDate(
          eventMock_notStarted.eventEndDate.toString()
        )}`
      );

      const eventDescription = within(votePage).queryByTestId('event-description');
      expect(eventDescription).not.toBeNull();
      expect(eventDescription.textContent).toEqual('Do you like pineapple pizza?');

      const options = within(votePage).queryAllByTestId('option-card');
      expect(options.length).toEqual(eventMock_notStarted.categories[0].proposals.length);
      for (const option in options) {
        expect(options[option].textContent).toEqual(
          capitalize(eventMock_notStarted.categories[0].proposals[option].name.toLowerCase())
        );
        expect(options[option].closest('button')).toHaveAttribute('disabled');
      }

      const cta = within(votePage).queryByTestId('event-hasnt-started-submit-button');
      expect(cta).not.toBeNull();
      expect(cta.textContent).toEqual(
        `Submit your vote from ${getDateAndMonth(eventMock_notStarted.eventStartDate?.toString())}`
      );
    });
  });
});

describe('For the event that has already finished', () => {
  beforeEach(() => {
    mockUseCardano.mockReturnValue(useCardanoMock);
    mockGetChainTip.mockReturnValue(chainTipMock);
    mockGetVoteReceipt.mockReturnValue({});
    mockGetUserInSession.mockReturnValue({ accessToken: true });
    mockTokenIsExpired.mockReturnValue(false);
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
      const votePage = screen.queryByTestId('vote-page');
      expect(votePage).not.toBeNull();

      const eventTitle = within(votePage).queryByTestId('event-title');
      expect(eventTitle).not.toBeNull();
      expect(eventTitle.textContent).toEqual('The Governance of Cardano');

      const eventTime = within(votePage).queryByTestId('event-time');
      expect(eventTime).not.toBeNull();
      expect(eventTime.textContent).toEqual(
        `The vote closed on ${formatUTCDate(eventMock_finished.eventEndDate.toString())}`
      );

      const eventDescription = within(votePage).queryByTestId('event-description');
      expect(eventDescription).not.toBeNull();
      expect(eventDescription.textContent).toEqual('Do you like pineapple pizza?');

      const options = within(votePage).queryAllByTestId('option-card');
      expect(options.length).toEqual(eventMock_finished.categories[0].proposals.length);
      for (const option in options) {
        expect(options[option].textContent).toEqual(
          capitalize(eventMock_finished.categories[0].proposals[option].name.toLowerCase())
        );
      }

      const cta = within(votePage).queryByTestId('proposal-connect-button');
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
      const votePage = screen.queryByTestId('vote-page');
      const cta = within(votePage).queryByTestId('proposal-connect-button');

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
      { preloadedState: { user: { event: eventMock_finished } as UserState } }
    );

    const votePage = await screen.findByTestId('vote-page');
    const options = within(votePage).queryAllByTestId('option-card');

    expect(store.getState().user.isConnectWalletModalVisible).toBeFalsy();
    fireEvent.click(options[0]);

    await waitFor(async () => {
      expect(store.getState().user.isConnectWalletModalVisible).toBeFalsy();
      expect(screen.queryAllByRole('button', { pressed: true }).length).toEqual(0);
    });
  });

  test('should render view result and open receipt cta when wallet is connected', async () => {
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

    await act(async () => {
      renderWithProviders(
        <CustomRouter history={history}>
          <VotePage />
        </CustomRouter>,
        {
          preloadedState: {
            user: {
              event: eventMock_finished,
              tip: chainTipMock,
            } as UserState,
          },
        }
      );
    });

    const votePage = screen.queryByTestId('vote-page');

    expect(screen.queryByTestId('vote-receipt')).not.toBeInTheDocument();
    const cta = within(votePage).queryByTestId('show-receipt-button');
    await act(async () => {
      fireEvent.click(cta);
    });

    expect(screen.queryByTestId('vote-receipt')).toBeInTheDocument();
    const closeVoteReceipt = within(screen.queryByTestId('vote-receipt')).queryByTestId('vote-receipt-close-button');
    await act(async () => {
      fireEvent.click(closeVoteReceipt);
    });
    await waitForElementToBeRemoved(() => screen.queryByTestId('vote-receipt'));

    const cta2 = within(votePage).queryByTestId('view-results-button');
    await act(async () => {
      fireEvent.click(cta2);
    });
    expect((historyPushSpy.mock.lastCall[0] as unknown as any).pathname).toEqual(ROUTES.LEADERBOARD);
    historyPushSpy.mockRestore();
  });
});
