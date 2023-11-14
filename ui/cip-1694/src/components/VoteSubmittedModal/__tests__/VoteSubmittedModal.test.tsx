/* eslint-disable no-var */
var mockDissmiss = jest.fn();
import React from 'react';
import { render, waitFor, screen, within, fireEvent } from '@testing-library/react';
import { createMemoryHistory } from 'history';
import { CustomRouter } from 'test/CustomRouter';
import { VoteSubmittedModal } from '../VoteSubmittedModal';

jest.mock('react-hot-toast', () => ({ dismiss: mockDissmiss }));

describe('VoteSubmittedModal:', () => {
  const props = {
    name: 'name',
    id: 'id',
    openStatus: true,
    title: 'title',
    description: 'description',
    onCloseFn: jest.fn(),
  };
  test('should render proper state', async () => {
    const history = createMemoryHistory({ initialEntries: ['/'] });

    render(
      <CustomRouter history={history}>
        <VoteSubmittedModal {...props} />
      </CustomRouter>
    );

    await waitFor(async () => {
      const modal = screen.queryByTestId('vote-submitted-modal');
      expect(modal).not.toBeNull();

      const title = within(modal).queryByTestId('vote-submitted-title');
      expect(title).not.toBeNull();
      expect(title.textContent).toEqual(props.title);

      const description = within(modal).queryByTestId('vote-submitted-description');
      expect(description).not.toBeNull();
      expect(description.textContent).toEqual(props.description);

      const cta = within(modal).queryByTestId('vote-submitted-cta');
      expect(cta).not.toBeNull();

      fireEvent.click(cta);
      expect(props.onCloseFn).toBeCalledTimes(1);
    });
  });
  test('should not render modal', async () => {
    const history = createMemoryHistory({ initialEntries: ['/'] });

    render(
      <CustomRouter history={history}>
        <VoteSubmittedModal
          {...props}
          openStatus={false}
        />
      </CustomRouter>
    );

    await waitFor(async () => {
      const modal = screen.queryByTestId('vote-submitted-modal');
      expect(modal).toBeNull();
    });
  });
});
