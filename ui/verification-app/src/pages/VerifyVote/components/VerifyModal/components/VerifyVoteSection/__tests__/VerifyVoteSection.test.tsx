import React, { useEffect, useState } from 'react';
import { render, waitFor, screen, fireEvent, act } from '@testing-library/react';
import { voteProofMock } from 'test/mocks';
import { VerifyVoteSection } from '../VerifyVoteSection';

jest.mock('@mui/material', () => ({
  ...jest.requireActual('@mui/material'),
  Fade: ({ onEntered, children }: { onEntered: () => void; children: React.ReactNode }) => {
    useEffect(() => {
      onEntered();
    });
    return children;
  },
  Slide: ({ children }: { children: React.ReactNode }) => children,
}));

describe('VerifyVoteSection:', () => {
  test('should render proper state', async () => {
    const voteProof = JSON.stringify(voteProofMock);
    const Wrapper = () => {
      const [value, setValue] = useState(voteProof);
      return (
        <VerifyVoteSection
          voteProof={value}
          setVoteProof={setValue}
        />
      );
    };
    render(<Wrapper />);

    const textarea = screen.queryByTestId('verify-vote-input').querySelector('textarea');
    expect(screen.queryByTestId('verify-vote-input').querySelector('textarea').textContent).toEqual(voteProof);

    expect(textarea).not.toBeNull();

    const newValue = 'newValue';

    await act(async () => {
      fireEvent.change(textarea, { target: { value: newValue } });
    });

    await waitFor(async () => {
      expect(screen.queryByTestId('verify-vote-input').querySelector('textarea').textContent).toEqual(newValue);
    });
  });
});
