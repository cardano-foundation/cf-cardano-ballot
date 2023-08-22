import React from 'react';
import { screen, render } from '@testing-library/react';
import SidePage from '../SidePage';

describe('SidePage:', () => {
  test('should render side drawer', async () => {
    const content = <>content</>;
    render(
      <SidePage
        anchor="right"
        open
        setOpen={jest.fn()}
      >
        {content}
      </SidePage>
    );

    expect((await screen.findByTestId('side-drawer')).textContent).toEqual('content');
  });
  test('should not render side drawer', async () => {
    const content = <>content</>;
    render(
      <SidePage
        anchor="right"
        open={false}
        setOpen={jest.fn()}
      >
        {content}
      </SidePage>
    );

    expect(screen.queryByTestId('side-drawer')).toBeNull();
  });
});
