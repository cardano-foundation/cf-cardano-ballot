/* eslint-disable no-var */
var VerifyModalMock = jest.fn();
var SuccessModalMock = jest.fn();
import React, { useEffect } from 'react';
import '@testing-library/jest-dom';
import { cleanup, render, screen } from '@testing-library/react';
import { VerifyVote } from '../VerifyVote';

jest.mock('../components/VerifyModal/VerifyModal', () => ({
  VerifyModal: VerifyModalMock,
}));

jest.mock('../components/SuccessModal/SuccessModal', () => ({
  SuccessModal: SuccessModalMock,
}));

describe('VerifyVote', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    cleanup();
  });

  test('should render modals', async () => {
    const explorerLink = 'explorerLink';
    VerifyModalMock.mockReset();
    VerifyModalMock.mockImplementation(({ onConfirm }: { onConfirm: (link: string) => void }) => {
      useEffect(() => {
        onConfirm(explorerLink);
      });
      return <span data-testid="verify-modal-mock" />;
    });
    SuccessModalMock.mockReset();
    SuccessModalMock.mockImplementation(() => <span data-testid="success-modal-mock" />);
    render(<VerifyVote />);

    expect(VerifyModalMock.mock.calls[0]).toEqual([{ opened: true, onConfirm: expect.any(Function) }, {}]);
    expect(VerifyModalMock.mock.calls[1]).toEqual([{ opened: false, onConfirm: expect.any(Function) }, {}]);
    expect(VerifyModalMock).toBeCalledTimes(2);

    expect(SuccessModalMock.mock.calls[0]).toEqual([{ opened: false, explorerLink: '' }, {}]);
    expect(SuccessModalMock.mock.calls[1]).toEqual([{ opened: true, explorerLink }, {}]);
    expect(SuccessModalMock).toBeCalledTimes(2);

    expect(screen.queryByTestId('verify-modal-mock')).toBeInTheDocument();
    expect(screen.queryByTestId('success-modal-mock')).toBeInTheDocument();
  });
});
