/* eslint-disable no-var */
var mockDissmiss = jest.fn();
var mockIcon = jest.fn();
import React from 'react';
import { render, waitFor, screen, within, fireEvent } from '@testing-library/react';
import { Toast } from '../Toast';

jest.mock('react-hot-toast', () => ({ dismiss: mockDissmiss }));

jest.mock('@mui/icons-material/CheckCircleOutlineOutlined', () => ({
  __esModule: true,
  default: mockIcon,
}));

describe('Toast:', () => {
  test('should render proper state', async () => {
    const message = 'message';
    const icon = 'message';
    render(
      <Toast
        message={message}
        icon={icon}
      />
    );

    await waitFor(async () => {
      const toast = screen.queryByTestId('toast');
      expect(toast).not.toBeNull();

      const toastMessage = within(toast).queryByTestId('toast-message');
      expect(toastMessage).not.toBeNull();
      expect(toastMessage.textContent).toEqual(message);

      const toastClose = within(toast).queryByTestId('toast-close-button');
      expect(toastClose).not.toBeNull();

      fireEvent.click(toastClose);
      expect(mockDissmiss).toBeCalledTimes(1);
    });
  });

  test('should render default Icon', async () => {
    mockIcon.mockReset();
    mockIcon.mockImplementation(() => <span data-testid="check-circle-outline-outlined" />);
    const message = 'message';
    render(<Toast message={message} />);

    await waitFor(async () => {
      expect(screen.queryByTestId('check-circle-outline-outlined')).not.toBeNull();
    });
  });
});
