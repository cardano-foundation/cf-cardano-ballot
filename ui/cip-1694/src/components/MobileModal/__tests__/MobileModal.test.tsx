/* eslint-disable no-var */
var mockDissmiss = jest.fn();
import React from 'react';
import { render, waitFor, screen, within, fireEvent } from '@testing-library/react';
import { MobileModal } from '../MobileModal';

jest.mock('react-hot-toast', () => ({ dismiss: mockDissmiss }));

describe('MobileModal:', () => {
  const props = {
    name: 'name',
    id: 'id',
    openStatus: true,
    title: 'title',
    children: 'children',
    onCloseFn: jest.fn(),
  };
  test('should render proper state', async () => {
    render(<MobileModal {...props} />);

    await waitFor(async () => {
      const modal = screen.queryByTestId('mobile-menu-modal');
      expect(modal).not.toBeNull();

      const title = within(modal).queryByTestId('mobile-menu-title');
      expect(title).not.toBeNull();
      expect(title.textContent).toEqual(props.title);

      const content = within(modal).queryByTestId('mobile-menu-content');
      expect(content).not.toBeNull();
      expect(content.textContent).toEqual(props.children);

      const cta = within(modal).queryByTestId('mobile-menu-cta');
      expect(cta).not.toBeNull();

      fireEvent.click(cta);
      expect(props.onCloseFn).toBeCalledTimes(1);
    });
  });
  test('should not render modal', async () => {
    render(
      <MobileModal
        {...props}
        openStatus={false}
      />
    );

    await waitFor(async () => {
      const modal = screen.queryByTestId('mobile-menu-modal');
      expect(modal).toBeNull();
    });
  });
});
