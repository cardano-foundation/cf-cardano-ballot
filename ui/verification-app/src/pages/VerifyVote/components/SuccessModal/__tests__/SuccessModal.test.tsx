import React, { useEffect } from 'react';
import { render, waitFor, screen, within } from '@testing-library/react';
import { SuccessModal } from '../SuccessModal';

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

describe('SuccessModal:', () => {
  test('should render proper state', async () => {
    const explorerLink = 'explorerLink';
    render(
      <SuccessModal
        opened
        explorerLink={explorerLink}
      />
    );

    await waitFor(async () => {
      const modal = screen.queryByTestId('success-modal');
      expect(within(modal).queryByTestId('success-modal-title').textContent).toEqual('Vote verified');
      expect(within(modal).queryByTestId('success-modal-description').textContent).toEqual(
        'Your vote has been successfully verified. Click the link or scan the QR code to view the transaction.'
      );
      const link = within(modal).queryByTestId('success-modal-link');
      expect(link.textContent).toEqual('View transaction details');
      expect(link.attributes.getNamedItem('href').value).toEqual(explorerLink);
    });
  });
});
