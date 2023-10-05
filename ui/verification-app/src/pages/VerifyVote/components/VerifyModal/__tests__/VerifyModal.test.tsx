/* eslint-disable no-var */
var mockToast = jest.fn();
var VerifyVoteSectionMock = jest.fn();
var ChoseExplorerSectionMock = jest.fn();
var mockVerifyVote = jest.fn();
import '@testing-library/jest-dom';
import React, { useEffect } from 'react';
import { cleanup, fireEvent, render, screen, waitFor, act } from '@testing-library/react';
import BlockIcon from '@mui/icons-material/Block';
import { Toast } from 'common/components/Toast/Toast';
import { voteProofMock } from 'test/mocks';
import { VerifyModal } from '../VerifyModal';
import { ERRORS, SECTIONS } from '../types';
import { titles, descriptions, ctas } from '../utils';
import { EXPLORERS } from '../components/ChoseExplorerSection/utils';

jest.mock('../components/VerifyVoteSection/VerifyVoteSection', () => ({
  VerifyVoteSection: VerifyVoteSectionMock,
}));

jest.mock('../components/ChoseExplorerSection/ChoseExplorerSection', () => ({
  ChoseExplorerSection: ChoseExplorerSectionMock,
}));

jest.mock('react-hot-toast', () => ({
  __esModule: true,
  ...jest.requireActual('react-hot-toast'),
  default: mockToast,
}));

jest.mock('common/api/verificationService', () => ({
  ...jest.requireActual('common/api/verificationService'),
  verifyVote: mockVerifyVote,
}));

describe('VerifyModal', () => {
  afterEach(() => {
    jest.clearAllMocks();
    cleanup();
  });

  test('should not render', async () => {
    render(
      <VerifyModal
        opened={false}
        onConfirm={jest.fn()}
      />
    );
  });

  test('should render proper state', async () => {
    VerifyVoteSectionMock.mockReset();
    VerifyVoteSectionMock.mockReturnValue(<span data-testid="verify-vote-section" />);
    ChoseExplorerSectionMock.mockReset();
    ChoseExplorerSectionMock.mockReturnValue(<span data-testid="chose-explorer-section" />);
    const onConfirm = jest.fn();
    render(
      <VerifyModal
        opened
        onConfirm={onConfirm}
      />
    );

    const title = screen.queryByTestId('verify-modal-title');
    expect(title).toBeInTheDocument();
    expect(title.textContent).toEqual(titles[SECTIONS.VERIFY]);

    const description = screen.queryByTestId('verify-modal-description');
    expect(description).toBeInTheDocument();
    expect(description.textContent).toEqual(descriptions[SECTIONS.VERIFY]);

    const verifyVoteSection = screen.queryByTestId('verify-vote-section');
    expect(verifyVoteSection).toBeInTheDocument();

    const choseExplorerSection = screen.queryByTestId('chose-explorer-section');
    expect(choseExplorerSection).not.toBeInTheDocument();

    const cta = screen.queryByTestId('verify-modal-cta');
    expect(cta).toBeInTheDocument();
    expect(cta.textContent).toEqual(ctas[SECTIONS.VERIFY]);
    expect(cta.closest('button')).toBeDisabled();
    fireEvent.click(cta);

    expect(onConfirm).not.toBeCalled();
    expect(mockVerifyVote).not.toBeCalled();
    expect(mockToast).not.toBeCalled();
  });

  test('should handle not valid JSON error', async () => {
    const parseSpy = jest.spyOn(JSON, 'parse');
    parseSpy.mockImplementation(() => {
      throw new Error('is not valid JSON');
    });
    VerifyVoteSectionMock.mockReset();
    VerifyVoteSectionMock.mockImplementation(({ setVoteProof }: { setVoteProof: (proof: string) => void }) => {
      useEffect(() => {
        setVoteProof(JSON.stringify('f'));
      });
      return <span data-testid="verify-vote-section" />;
    });
    const onConfirm = jest.fn();
    render(
      <VerifyModal
        opened
        onConfirm={onConfirm}
      />
    );

    const cta = screen.queryByTestId('verify-modal-cta');
    expect(cta).toBeInTheDocument();
    expect(cta.textContent).toEqual(ctas[SECTIONS.VERIFY]);
    expect(cta.closest('button')).not.toBeDisabled();

    fireEvent.click(cta);

    await waitFor(async () => {
      expect(mockToast).toBeCalledWith(
        <Toast
          error={true}
          icon={<BlockIcon style={{ color: '#F5F9FF', fontSize: '19px' }} />}
          message="Invalid JSON. Please try again"
        />
      );

      expect(screen.queryByTestId('verify-modal-cta').textContent).toEqual(ctas[SECTIONS.VERIFY]);
      expect(screen.queryByTestId('verify-modal-cta').closest('button')).not.toBeDisabled();
      expect(screen.queryByTestId('verify-vote-section')).toBeInTheDocument();
      expect(screen.queryByTestId('chose-explorer-section')).not.toBeInTheDocument();
      expect(mockVerifyVote).not.toBeCalled();
    });
    parseSpy.mockRestore();
  });

  test('should handle unsuported event error', async () => {
    mockVerifyVote.mockReset();
    mockVerifyVote.mockImplementation(async () => await Promise.reject(new Error(ERRORS.UNSUPPORTED_EVENT)));
    VerifyVoteSectionMock.mockReset();
    VerifyVoteSectionMock.mockImplementation(({ setVoteProof }: { setVoteProof: (proof: string) => void }) => {
      useEffect(() => {
        setVoteProof(JSON.stringify(voteProofMock));
      });
      return <span data-testid="verify-vote-section" />;
    });
    const onConfirm = jest.fn();
    render(
      <VerifyModal
        opened
        onConfirm={onConfirm}
      />
    );

    fireEvent.click(screen.queryByTestId('verify-modal-cta'));

    await waitFor(async () => {
      expect(mockToast).toBeCalledWith(
        <Toast
          error={true}
          icon={<BlockIcon style={{ color: '#F5F9FF', fontSize: '19px' }} />}
          message="Unsupported event"
        />
      );
      expect(screen.queryByTestId('verify-modal-cta').closest('button')).not.toBeDisabled();
      expect(screen.queryByTestId('verify-modal-cta').textContent).toEqual(ctas[SECTIONS.VERIFY]);
      expect(screen.queryByTestId('verify-vote-section')).toBeInTheDocument();
      expect(screen.queryByTestId('chose-explorer-section')).not.toBeInTheDocument();
      expect(mockVerifyVote).toBeCalledTimes(1);
      expect(mockVerifyVote).toBeCalledWith({
        rootHash: voteProofMock.rootHash,
        voteCoseSignature: voteProofMock.coseSignature,
        voteCosePublicKey: voteProofMock.cosePublicKey,
        steps: voteProofMock.steps,
      });
    });
  });

  test('should handle other verify errors', async () => {
    mockVerifyVote.mockReset();
    mockVerifyVote.mockImplementation(async () => await Promise.reject(new Error('error')));
    VerifyVoteSectionMock.mockReset();
    VerifyVoteSectionMock.mockImplementation(({ setVoteProof }: { setVoteProof: (proof: string) => void }) => {
      useEffect(() => {
        setVoteProof(JSON.stringify(voteProofMock));
      });
      return <span data-testid="verify-vote-section" />;
    });
    const onConfirm = jest.fn();
    render(
      <VerifyModal
        opened
        onConfirm={onConfirm}
      />
    );

    fireEvent.click(screen.queryByTestId('verify-modal-cta'));

    await waitFor(async () => {
      expect(mockToast).toBeCalledWith(
        <Toast
          error={true}
          icon={<BlockIcon style={{ color: '#F5F9FF', fontSize: '19px' }} />}
          message="Unable to verify vote"
        />
      );
      expect(screen.queryByTestId('verify-modal-cta').closest('button')).not.toBeDisabled();
      expect(screen.queryByTestId('verify-modal-cta').textContent).toEqual(ctas[SECTIONS.VERIFY]);
      expect(screen.queryByTestId('verify-vote-section')).toBeInTheDocument();
      expect(screen.queryByTestId('chose-explorer-section')).not.toBeInTheDocument();
      expect(mockVerifyVote).toBeCalledTimes(1);
      expect(mockVerifyVote).toBeCalledWith({
        rootHash: voteProofMock.rootHash,
        voteCoseSignature: voteProofMock.coseSignature,
        voteCosePublicKey: voteProofMock.cosePublicKey,
        steps: voteProofMock.steps,
      });
    });
  });

  test('should handle not verified vote', async () => {
    mockVerifyVote.mockReset();
    mockVerifyVote.mockImplementation(async () => await Promise.resolve({ verified: false }));
    VerifyVoteSectionMock.mockReset();
    VerifyVoteSectionMock.mockImplementation(({ setVoteProof }: { setVoteProof: (proof: string) => void }) => {
      useEffect(() => {
        setVoteProof(JSON.stringify(voteProofMock));
      });
      return <span data-testid="verify-vote-section" />;
    });
    const onConfirm = jest.fn();
    render(
      <VerifyModal
        opened
        onConfirm={onConfirm}
      />
    );

    fireEvent.click(screen.queryByTestId('verify-modal-cta'));

    await waitFor(async () => {
      expect(mockToast).toBeCalledWith(
        <Toast
          error={true}
          icon={<BlockIcon style={{ color: '#F5F9FF', fontSize: '19px' }} />}
          message="Vote is not verified. Please check again later"
        />
      );
      expect(screen.queryByTestId('verify-modal-cta').closest('button')).not.toBeDisabled();
      expect(screen.queryByTestId('verify-modal-cta').textContent).toEqual(ctas[SECTIONS.VERIFY]);
      expect(screen.queryByTestId('verify-vote-section')).toBeInTheDocument();
      expect(screen.queryByTestId('chose-explorer-section')).not.toBeInTheDocument();
      expect(mockVerifyVote).toBeCalledTimes(1);
      expect(mockVerifyVote).toBeCalledWith({
        rootHash: voteProofMock.rootHash,
        voteCoseSignature: voteProofMock.coseSignature,
        voteCosePublicKey: voteProofMock.cosePublicKey,
        steps: voteProofMock.steps,
      });
    });
  });

  test('should successfully verify vote and switch to chose explorer section', async () => {
    mockVerifyVote.mockImplementation(async () => await Promise.resolve({ verified: true }));
    VerifyVoteSectionMock.mockReset();
    VerifyVoteSectionMock.mockImplementation(({ setVoteProof }: { setVoteProof: (proof: string) => void }) => {
      useEffect(() => {
        setVoteProof(JSON.stringify(voteProofMock));
      });
      return <span data-testid="verify-vote-section" />;
    });
    const onConfirm = jest.fn();
    render(
      <VerifyModal
        opened
        onConfirm={onConfirm}
      />
    );

    await act(async () => {
      fireEvent.click(screen.queryByTestId('verify-modal-cta'));
    });

    expect(screen.queryByTestId('verify-modal-cta').closest('button')).toBeDisabled();
    expect(screen.queryByTestId('verify-modal-cta').textContent).toEqual(ctas[SECTIONS.CHOSE_EXPLORER]);
    expect(screen.queryByTestId('verify-vote-section')).not.toBeInTheDocument();
    expect(screen.queryByTestId('chose-explorer-section')).toBeInTheDocument();

    await act(async () => {
      fireEvent.click(screen.queryByTestId('verify-modal-cta'));
    });
    expect(onConfirm).not.toBeCalled();
    expect(mockVerifyVote).toBeCalledTimes(1);
    expect(mockVerifyVote).toBeCalledWith({
      rootHash: voteProofMock.rootHash,
      voteCoseSignature: voteProofMock.coseSignature,
      voteCosePublicKey: voteProofMock.cosePublicKey,
      steps: voteProofMock.steps,
    });
  });

  test('should call onConfirm for selected ', async () => {
    mockVerifyVote.mockImplementation(async () => await Promise.resolve({ verified: true }));
    VerifyVoteSectionMock.mockReset();
    VerifyVoteSectionMock.mockImplementation(({ setVoteProof }: { setVoteProof: (proof: string) => void }) => {
      useEffect(() => {
        setVoteProof(JSON.stringify(voteProofMock));
      });
      return <span data-testid="verify-vote-section" />;
    });

    ChoseExplorerSectionMock.mockReset();
    ChoseExplorerSectionMock.mockImplementation(({ setExplorer }: { setExplorer: (link: string) => void }) => {
      useEffect(() => {
        setExplorer(EXPLORERS[0].url);
      });
      return <span data-testid="chose-explorer-section" />;
    });
    const onConfirm = jest.fn();
    render(
      <VerifyModal
        opened
        onConfirm={onConfirm}
      />
    );

    await act(async () => {
      fireEvent.click(screen.queryByTestId('verify-modal-cta'));
    });

    expect(screen.queryByTestId('verify-modal-cta').closest('button')).not.toBeDisabled();
    expect(screen.queryByTestId('verify-modal-cta').textContent).toEqual(ctas[SECTIONS.CHOSE_EXPLORER]);
    expect(screen.queryByTestId('verify-vote-section')).not.toBeInTheDocument();
    expect(screen.queryByTestId('chose-explorer-section')).toBeInTheDocument();
    expect(mockVerifyVote).toBeCalledTimes(1);
    expect(mockVerifyVote).toBeCalledWith({
      rootHash: voteProofMock.rootHash,
      voteCoseSignature: voteProofMock.coseSignature,
      voteCosePublicKey: voteProofMock.cosePublicKey,
      steps: voteProofMock.steps,
    });

    await act(async () => {
      fireEvent.click(screen.queryByTestId('verify-modal-cta'));
    });
    expect(onConfirm).toBeCalledWith(`${EXPLORERS[0].url}${voteProofMock.transactionHash}`);
  });
});
