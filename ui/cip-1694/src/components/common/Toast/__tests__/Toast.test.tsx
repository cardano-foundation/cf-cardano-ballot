/* eslint-disable no-var */
var mockDissmiss = jest.fn();
import React from 'react';
import { render, waitFor, screen, within, fireEvent } from '@testing-library/react';
import { Toast } from '../Toast';

jest.mock('react-hot-toast', () => ({ dismiss: mockDissmiss }));

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
      const toast = await screen.queryByTestId('toast');
      expect(toast).not.toBeNull();

      const toastMessage = await within(toast).queryByTestId('toast-message');
      expect(toastMessage).not.toBeNull();
      expect(toastMessage.textContent).toEqual(message);

      const toastClose = await within(toast).queryByTestId('toast-close-button');
      expect(toastClose).not.toBeNull();

      fireEvent.click(toastClose);
      expect(mockDissmiss).toBeCalledTimes(1);
    });
  });
});
