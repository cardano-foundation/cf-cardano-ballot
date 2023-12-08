/* eslint-disable no-var */
var mockVerifyVote = jest.fn();
var mockToast = jest.fn();
var mockJsonViewer = jest.fn();
import React from 'react';
import '@testing-library/jest-dom';
import { expect } from '@jest/globals';
import { cleanup, act, waitFor, screen, within, fireEvent } from '@testing-library/react';
import { createMemoryHistory } from 'history';
import BlockIcon from '@mui/icons-material/Block';
import { ROUTES } from 'common/routes';
import { Toast } from 'components/Toast/Toast';
import { renderWithProviders } from 'test/mockProviders';
import { CustomRouter } from 'test/CustomRouter';
import {
  VoteReceiptMock_Basic,
  VoteReceiptMock_Full_MediumAssurance,
  VoteReceiptMock_Partial,
  VoteReceiptMock_Rollback,
  VoteReceiptMock_Full_LowAssurance,
  VoteReceiptMock_Full_HighAssurance,
} from 'test/mocks';
import { VoteReceipt } from '../VoteReceipt';
import { shortenString } from '../utils';

jest.mock('react-hot-toast', () => mockToast);

jest.mock('@textea/json-viewer', () => ({
  JsonViewer: mockJsonViewer,
}));

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

jest.mock('common/api/verificationService', () => ({
  ...jest.requireActual('common/api/verificationService'),
  verifyVote: mockVerifyVote,
}));

describe('Ballot receipt:', () => {
  const jsdomAlert = window.prompt;
  beforeAll(() => {
    window.prompt = (message?: string) => message;
  });
  beforeEach(() => {
    jest.clearAllMocks();
    cleanup();
  });
  afterAll(() => {
    window.prompt = jsdomAlert;
  });
  describe('should handle error scenarios:', () => {
    test('should display proper message if fails to verify ballot', async () => {
      mockVerifyVote.mockImplementation(async () => await Promise.reject('error'));

      const history = createMemoryHistory({ initialEntries: [ROUTES.INTRO] });
      await act(async () =>
        renderWithProviders(
          <CustomRouter history={history}>
            <VoteReceipt
              setOpen={jest.fn()}
              fetchReceipt={jest.fn()}
              receipt={VoteReceiptMock_Full_MediumAssurance}
            />
          </CustomRouter>
        )
      );

      await waitFor(() => {
        expect(mockToast).toBeCalledWith(
          <Toast
            message="Unable to verify ballot receipt. Please try again"
            error
            icon={<BlockIcon style={{ fontSize: '19px', color: '#F5F9FF' }} />}
          />
        );
      });
    });
  });

  describe('BASIC', () => {
    test('should render proper state', async () => {
      const fetchReceiptMock = jest.fn();
      const history = createMemoryHistory({ initialEntries: [ROUTES.INTRO] });
      await act(async () =>
        renderWithProviders(
          <CustomRouter history={history}>
            <VoteReceipt
              setOpen={jest.fn()}
              fetchReceipt={fetchReceiptMock}
              receipt={VoteReceiptMock_Basic}
            />
          </CustomRouter>
        )
      );

      const voteReceipt = await screen.findByTestId('vote-receipt');
      expect(voteReceipt).toBeInTheDocument();
      expect(within(voteReceipt).queryByTestId('vote-receipt-title')).toHaveTextContent('Ballot Receipt');

      const receiptInfo = within(voteReceipt).queryByTestId('receipt-info');
      expect(within(receiptInfo).queryByTestId('receipt-info-title')).toHaveTextContent(
        'Ballot not ready for verification'
      );
      expect(within(receiptInfo).queryByTestId('receipt-info-description')).toHaveTextContent(
        'Your ballot has been successfully submitted. You might have to wait up to 30 minutes for this to be visible on-chain. Please check back later to verify your ballot.'
      );

      fireEvent.click(within(receiptInfo).queryByTestId('refetch-receipt-button'));

      expect(fetchReceiptMock).toBeCalledTimes(1);

      const receiptDataTitles = within(voteReceipt).queryAllByTestId('receipt-item-title');
      const receiptDataValues = within(voteReceipt).queryAllByTestId('receipt-item-value');

      const expectedDataValues = [
        { title: 'Event', value: VoteReceiptMock_Basic.event },
        { title: 'Proposal', value: VoteReceiptMock_Basic.proposal },
        { title: 'Voting Power', value: VoteReceiptMock_Basic.votingPower },
        { title: 'Voter Staking Address', value: shortenString(VoteReceiptMock_Basic.voterStakingAddress, 5, 5) },
        { title: 'Status', value: VoteReceiptMock_Basic.status },
      ];

      expect(receiptDataTitles.length).toEqual(expectedDataValues.length);
      expect(receiptDataValues.length).toEqual(expectedDataValues.length);

      for (const itemIndex in expectedDataValues) {
        expect(receiptDataTitles[itemIndex]).toHaveTextContent(expectedDataValues[itemIndex].title);
        expect(receiptDataValues[itemIndex]).toHaveTextContent(expectedDataValues[itemIndex].value);
      }

      const expectedExtendedDataValues = [
        { title: 'ID', value: shortenString(VoteReceiptMock_Basic.id, 8, 12) },
        { title: 'Ballot submitted at Slot', value: VoteReceiptMock_Basic.votedAtSlot },
      ];

      expect(within(voteReceipt).queryAllByTestId('receipt-item-extended-title').length).toEqual(0);
      expect(within(voteReceipt).queryAllByTestId('receipt-item-extended-value').length).toEqual(0);

      fireEvent.click(within(voteReceipt).queryByTestId('receipt-item-accordion'));

      const receiptDataExtendedTitles = await within(voteReceipt).findAllByTestId('receipt-item-extended-title');
      const receiptDataExtendedValues = await within(voteReceipt).findAllByTestId('receipt-item-extended-value');

      expect(receiptDataExtendedTitles.length).toEqual(expectedExtendedDataValues.length);
      expect(receiptDataExtendedValues.length).toEqual(expectedExtendedDataValues.length);
      for (const itemIndex in expectedExtendedDataValues) {
        expect(receiptDataExtendedTitles[itemIndex]).toHaveTextContent(expectedExtendedDataValues[itemIndex].title);
        expect(receiptDataExtendedValues[itemIndex]).toHaveTextContent(expectedExtendedDataValues[itemIndex].value);
      }

      await waitFor(() => {
        expect(mockToast).not.toBeCalled();
      });
    });
  });

  describe('PARTIAL', () => {
    test('should render proper state', async () => {
      const fetchReceiptMock = jest.fn();
      const history = createMemoryHistory({ initialEntries: [ROUTES.INTRO] });
      await act(async () =>
        renderWithProviders(
          <CustomRouter history={history}>
            <VoteReceipt
              setOpen={jest.fn()}
              fetchReceipt={fetchReceiptMock}
              receipt={VoteReceiptMock_Partial}
            />
          </CustomRouter>
        )
      );

      const voteReceipt = await screen.findByTestId('vote-receipt');
      expect(voteReceipt).toBeInTheDocument();
      expect(within(voteReceipt).queryByTestId('vote-receipt-title')).toHaveTextContent('Ballot Receipt');

      const receiptInfo = within(voteReceipt).queryByTestId('receipt-info');
      expect(within(receiptInfo).queryByTestId('receipt-info-title')).toHaveTextContent('Ballot in progress');
      expect(within(receiptInfo).queryByTestId('receipt-info-description')).toHaveTextContent(
        'Your transaction has been sent and is awaiting confirmation from the Cardano network (this could be 5-10 minutes). Once this has been confirmed you’ll be able to verify your ballot.'
      );

      fireEvent.click(within(receiptInfo).queryByTestId('refetch-receipt-button'));

      expect(fetchReceiptMock).toBeCalledTimes(1);

      const receiptDataTitles = within(voteReceipt).queryAllByTestId('receipt-item-title');
      const receiptDataValues = within(voteReceipt).queryAllByTestId('receipt-item-value');

      const expectedDataValues = [
        { title: 'Event', value: VoteReceiptMock_Partial.event },
        { title: 'Proposal', value: VoteReceiptMock_Partial.proposal },
        { title: 'Voting Power', value: VoteReceiptMock_Partial.votingPower },
        { title: 'Voter Staking Address', value: shortenString(VoteReceiptMock_Partial.voterStakingAddress, 5, 5) },
        { title: 'Status', value: VoteReceiptMock_Partial.status },
      ];

      expect(receiptDataTitles.length).toEqual(expectedDataValues.length);
      expect(receiptDataValues.length).toEqual(expectedDataValues.length);

      for (const itemIndex in expectedDataValues) {
        expect(receiptDataTitles[itemIndex]).toHaveTextContent(expectedDataValues[itemIndex].title);
        expect(receiptDataValues[itemIndex]).toHaveTextContent(expectedDataValues[itemIndex].value);
      }

      const expectedExtendedDataValues = [
        { title: 'ID', value: shortenString(VoteReceiptMock_Partial.id, 8, 12) },
        { title: 'Ballot submitted at Slot', value: VoteReceiptMock_Partial.votedAtSlot },
      ];

      expect(within(voteReceipt).queryAllByTestId('receipt-item-extended-title').length).toEqual(0);
      expect(within(voteReceipt).queryAllByTestId('receipt-item-extended-value').length).toEqual(0);

      fireEvent.click(within(voteReceipt).queryByTestId('receipt-item-accordion'));

      const receiptDataExtendedTitles = await within(voteReceipt).findAllByTestId('receipt-item-extended-title');
      const receiptDataExtendedValues = await within(voteReceipt).findAllByTestId('receipt-item-extended-value');

      expect(receiptDataExtendedTitles.length).toEqual(expectedExtendedDataValues.length);
      expect(receiptDataExtendedValues.length).toEqual(expectedExtendedDataValues.length);
      for (const itemIndex in expectedExtendedDataValues) {
        expect(receiptDataExtendedTitles[itemIndex]).toHaveTextContent(expectedExtendedDataValues[itemIndex].title);
        expect(receiptDataExtendedValues[itemIndex]).toHaveTextContent(expectedExtendedDataValues[itemIndex].value);
      }

      await waitFor(() => {
        expect(mockToast).not.toBeCalled();
      });
    });
  });

  describe('ROLLBACK', () => {
    test('should render proper state', async () => {
      const fetchReceiptMock = jest.fn();
      const history = createMemoryHistory({ initialEntries: [ROUTES.INTRO] });
      await act(async () =>
        renderWithProviders(
          <CustomRouter history={history}>
            <VoteReceipt
              setOpen={jest.fn()}
              fetchReceipt={fetchReceiptMock}
              receipt={VoteReceiptMock_Rollback}
            />
          </CustomRouter>
        )
      );

      const voteReceipt = await screen.findByTestId('vote-receipt');
      expect(voteReceipt).toBeInTheDocument();
      expect(within(voteReceipt).queryByTestId('vote-receipt-title')).toHaveTextContent('Ballot Receipt');

      const receiptInfo = within(voteReceipt).queryByTestId('receipt-info');
      expect(within(receiptInfo).queryByTestId('receipt-info-title')).toHaveTextContent('There’s been a rollback');
      expect(within(receiptInfo).queryByTestId('receipt-info-description')).toHaveTextContent(
        'Don’t worry there’s nothing for you to do. We will automatically resubmit your ballot. Please check back later (up to 30 minutes) to see your ballot status.'
      );

      fireEvent.click(within(receiptInfo).queryByTestId('refetch-receipt-button'));

      expect(fetchReceiptMock).toBeCalledTimes(1);

      const receiptDataTitles = within(voteReceipt).queryAllByTestId('receipt-item-title');
      const receiptDataValues = within(voteReceipt).queryAllByTestId('receipt-item-value');

      const expectedDataValues = [
        { title: 'Event', value: VoteReceiptMock_Rollback.event },
        { title: 'Proposal', value: VoteReceiptMock_Rollback.proposal },
        { title: 'Voting Power', value: VoteReceiptMock_Rollback.votingPower },
        { title: 'Voter Staking Address', value: shortenString(VoteReceiptMock_Rollback.voterStakingAddress, 5, 5) },
        { title: 'Status', value: VoteReceiptMock_Rollback.status },
      ];

      expect(receiptDataTitles.length).toEqual(expectedDataValues.length);
      expect(receiptDataValues.length).toEqual(expectedDataValues.length);

      for (const itemIndex in expectedDataValues) {
        expect(receiptDataTitles[itemIndex]).toHaveTextContent(expectedDataValues[itemIndex].title);
        expect(receiptDataValues[itemIndex]).toHaveTextContent(expectedDataValues[itemIndex].value);
      }

      const expectedExtendedDataValues = [
        { title: 'ID', value: shortenString(VoteReceiptMock_Rollback.id, 8, 12) },
        { title: 'Ballot submitted at Slot', value: VoteReceiptMock_Rollback.votedAtSlot },
      ];

      expect(within(voteReceipt).queryAllByTestId('receipt-item-extended-title').length).toEqual(0);
      expect(within(voteReceipt).queryAllByTestId('receipt-item-extended-value').length).toEqual(0);

      fireEvent.click(within(voteReceipt).queryByTestId('receipt-item-accordion'));

      const receiptDataExtendedTitles = await within(voteReceipt).findAllByTestId('receipt-item-extended-title');
      const receiptDataExtendedValues = await within(voteReceipt).findAllByTestId('receipt-item-extended-value');

      expect(receiptDataExtendedTitles.length).toEqual(expectedExtendedDataValues.length);
      expect(receiptDataExtendedValues.length).toEqual(expectedExtendedDataValues.length);
      for (const itemIndex in expectedExtendedDataValues) {
        expect(receiptDataExtendedTitles[itemIndex]).toHaveTextContent(expectedExtendedDataValues[itemIndex].title);
        expect(receiptDataExtendedValues[itemIndex]).toHaveTextContent(expectedExtendedDataValues[itemIndex].value);
      }

      await waitFor(() => {
        expect(mockToast).not.toBeCalled();
      });
    });
  });

  describe('FULL', () => {
    test('should render proper state', async () => {
      mockVerifyVote.mockImplementation(async () => await Promise.resolve({ verified: false }));
      const JsonViewerContentMock = 'JsonViewerContent';
      mockJsonViewer.mockImplementation(() => <span>{JsonViewerContentMock}</span>);
      const fetchReceiptMock = jest.fn();
      const history = createMemoryHistory({ initialEntries: [ROUTES.INTRO] });
      await act(async () =>
        renderWithProviders(
          <CustomRouter history={history}>
            <VoteReceipt
              setOpen={jest.fn()}
              fetchReceipt={fetchReceiptMock}
              receipt={VoteReceiptMock_Full_LowAssurance}
            />
          </CustomRouter>
        )
      );

      const voteReceipt = await screen.findByTestId('vote-receipt');
      expect(voteReceipt).toBeInTheDocument();
      expect(within(voteReceipt).queryByTestId('vote-receipt-title')).toHaveTextContent('Ballot Receipt');

      const receiptInfo = within(voteReceipt).queryByTestId('receipt-info');

      fireEvent.click(within(receiptInfo).queryByTestId('refetch-receipt-button'));

      expect(fetchReceiptMock).toBeCalledTimes(1);

      const receiptDataTitles = within(voteReceipt).queryAllByTestId('receipt-item-title');
      const receiptDataValues = within(voteReceipt).queryAllByTestId('receipt-item-value');

      const expectedDataValues = [
        { title: 'Event', value: VoteReceiptMock_Full_LowAssurance.event },
        { title: 'Proposal', value: VoteReceiptMock_Full_LowAssurance.proposal },
        { title: 'Voting Power', value: VoteReceiptMock_Full_LowAssurance.votingPower },
        {
          title: 'Voter Staking Address',
          value: shortenString(VoteReceiptMock_Full_LowAssurance.voterStakingAddress, 5, 5),
        },
        { title: 'Status', value: VoteReceiptMock_Full_LowAssurance.status },
      ];

      expect(receiptDataTitles.length).toEqual(expectedDataValues.length);
      expect(receiptDataValues.length).toEqual(expectedDataValues.length);

      for (const itemIndex in expectedDataValues) {
        expect(receiptDataTitles[itemIndex]).toHaveTextContent(expectedDataValues[itemIndex].title);
        expect(receiptDataValues[itemIndex]).toHaveTextContent(expectedDataValues[itemIndex].value);
      }

      const voteProof = {
        transactionHash: VoteReceiptMock_Full_LowAssurance?.merkleProof?.transactionHash,
        rootHash: VoteReceiptMock_Full_LowAssurance?.merkleProof?.rootHash,
        steps: VoteReceiptMock_Full_LowAssurance?.merkleProof?.steps,
        coseSignature: VoteReceiptMock_Full_LowAssurance.coseSignature,
        cosePublicKey: VoteReceiptMock_Full_LowAssurance.cosePublicKey,
      };
      const expectedExtendedDataValues = [
        { title: 'ID', value: shortenString(VoteReceiptMock_Full_LowAssurance.id, 8, 12) },
        { title: 'Ballot submitted at Slot', value: VoteReceiptMock_Full_LowAssurance.votedAtSlot },
        { title: 'Ballot Proof', value: JsonViewerContentMock },
      ];

      expect(within(voteReceipt).queryAllByTestId('receipt-item-extended-title').length).toEqual(0);
      expect(within(voteReceipt).queryAllByTestId('receipt-item-extended-value').length).toEqual(0);

      fireEvent.click(within(voteReceipt).queryByTestId('receipt-item-accordion'));

      const receiptDataExtendedTitles = await within(voteReceipt).findAllByTestId('receipt-item-extended-title');
      const receiptDataExtendedValues = await within(voteReceipt).findAllByTestId('receipt-item-extended-value');

      expect(receiptDataExtendedTitles.length).toEqual(expectedExtendedDataValues.length);
      expect(receiptDataExtendedValues.length).toEqual(expectedExtendedDataValues.length);
      for (const itemIndex in expectedExtendedDataValues) {
        expect(receiptDataExtendedTitles[itemIndex]).toHaveTextContent(expectedExtendedDataValues[itemIndex].title);
        expect(receiptDataExtendedValues[itemIndex]).toHaveTextContent(expectedExtendedDataValues[itemIndex].value);
      }

      await waitFor(() => {
        expect(mockToast).not.toBeCalled();
        expect(mockVerifyVote).toBeCalled();
        expect(mockJsonViewer).toBeCalledWith({ value: voteProof, enableClipboard: false }, {});
      });

      fireEvent.click(within(voteReceipt).queryByTestId('copy-vote-proof-cta'));
      await waitFor(async () => {
        expect(mockToast).toBeCalledWith(<Toast message="Copied to clipboard" />);
      });
    });

    describe('LOW', () => {
      test('should render proper state', async () => {
        const history = createMemoryHistory({ initialEntries: [ROUTES.INTRO] });
        await act(async () =>
          renderWithProviders(
            <CustomRouter history={history}>
              <VoteReceipt
                setOpen={jest.fn()}
                fetchReceipt={jest.fn()}
                receipt={VoteReceiptMock_Full_LowAssurance}
              />
            </CustomRouter>
          )
        );

        const voteReceipt = await screen.findByTestId('vote-receipt');
        expect(voteReceipt).toBeInTheDocument();

        const receiptInfo = within(voteReceipt).queryByTestId('receipt-info');
        expect(within(receiptInfo).queryByTestId('receipt-info-title')).toHaveTextContent('Assurance: LOW');
        expect(within(receiptInfo).queryByTestId('receipt-info-description')).toHaveTextContent(
          'Your ballot is currently being verified. While in LOW, there is the highest chance of a rollback. Check back later to see if verification has completed.'
        );
      });
    });
    describe('MEDIUM', () => {
      test('should render proper state', async () => {
        const history = createMemoryHistory({ initialEntries: [ROUTES.INTRO] });
        await act(async () =>
          renderWithProviders(
            <CustomRouter history={history}>
              <VoteReceipt
                setOpen={jest.fn()}
                fetchReceipt={jest.fn()}
                receipt={VoteReceiptMock_Full_MediumAssurance}
              />
            </CustomRouter>
          )
        );

        const voteReceipt = await screen.findByTestId('vote-receipt');
        expect(voteReceipt).toBeInTheDocument();

        const receiptInfo = within(voteReceipt).queryByTestId('receipt-info');
        expect(within(receiptInfo).queryByTestId('receipt-info-title')).toHaveTextContent('Assurance: MEDIUM');
        expect(within(receiptInfo).queryByTestId('receipt-info-description')).toHaveTextContent(
          'Your ballot is currently being verified. While in MEDIUM, the chance of rollback is still possible. Check back later to see if verification has completed.'
        );
      });
    });
    describe('HIGH', () => {
      test('should render proper state', async () => {
        const history = createMemoryHistory({ initialEntries: [ROUTES.INTRO] });
        await act(async () =>
          renderWithProviders(
            <CustomRouter history={history}>
              <VoteReceipt
                setOpen={jest.fn()}
                fetchReceipt={jest.fn()}
                receipt={VoteReceiptMock_Full_HighAssurance}
              />
            </CustomRouter>
          )
        );

        const voteReceipt = await screen.findByTestId('vote-receipt');
        expect(voteReceipt).toBeInTheDocument();

        const receiptInfo = within(voteReceipt).queryByTestId('receipt-info');
        expect(within(receiptInfo).queryByTestId('receipt-info-title')).toHaveTextContent('Assurance: HIGH');
        expect(within(receiptInfo).queryByTestId('receipt-info-description')).toHaveTextContent(
          'Your ballot is currently being verified. While in HIGH, the chance of a rollback is very unlikely. Check back later to see if verification has completed.'
        );
      });
    });
    describe('VERIFIED', () => {
      test('should render proper state', async () => {
        mockVerifyVote.mockImplementation(async () => await Promise.resolve({ verified: true }));
        const history = createMemoryHistory({ initialEntries: [ROUTES.INTRO] });
        await act(async () =>
          renderWithProviders(
            <CustomRouter history={history}>
              <VoteReceipt
                setOpen={jest.fn()}
                fetchReceipt={jest.fn()}
                receipt={VoteReceiptMock_Full_HighAssurance}
              />
            </CustomRouter>
          )
        );

        const voteReceipt = await screen.findByTestId('vote-receipt');
        expect(voteReceipt).toBeInTheDocument();

        const receiptInfo = within(voteReceipt).queryByTestId('receipt-info');
        await waitFor(() => {
          expect(within(receiptInfo).queryByTestId('receipt-info-title')).toHaveTextContent('Verified');
          expect(within(receiptInfo).queryByTestId('receipt-info-description')).toHaveTextContent('');
        });
      });
    });
  });
});
