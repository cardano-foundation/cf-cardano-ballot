/* eslint-disable no-var */
var mockQRCodeStyling = jest.fn();
import React from 'react';
import { render, waitFor, screen } from '@testing-library/react';
import { QRCode, defaultOptions } from '../QRCode';

jest.mock('qr-code-styling', () => ({
  __esModule: true,
  ...jest.requireActual('qr-code-styling'),
  default: mockQRCodeStyling,
}));

describe('QRCode:', () => {
  test('should render proper state', async () => {
    const data = 'data';
    const options = { margin: 5 };
    const appendMock = jest.fn();
    const updateMock = jest.fn();
    mockQRCodeStyling.mockReset();
    mockQRCodeStyling.mockImplementation(() => ({
      append: appendMock,
      update: updateMock,
    }));
    render(
      <QRCode
        data={data}
        options={options}
      />
    );

    await waitFor(async () => {
      const toast = await screen.queryByTestId('qr-code');
      expect(appendMock).toBeCalledTimes(1);
      expect(appendMock).toBeCalledWith(toast);
      expect(updateMock).toBeCalledTimes(1);
      expect(updateMock).toBeCalledWith({ ...defaultOptions, ...options, data });
    });
  });
});
