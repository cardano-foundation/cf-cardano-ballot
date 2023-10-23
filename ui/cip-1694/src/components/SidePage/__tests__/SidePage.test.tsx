import React, { useState } from 'react';
import { screen, render, fireEvent, act, waitFor } from '@testing-library/react';
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

  test('should handle close event', async () => {
    const content = <>content</>;
    const Wrapped = () => {
      const [open, setOpen] = useState(true);

      return (
        <SidePage
          anchor="right"
          open={open}
          setOpen={setOpen}
        >
          {content}
        </SidePage>
      );
    };
    render(<Wrapped />);
    expect(screen.queryByTestId('side-drawer')).not.toBeNull();

    await act(async () => {
      fireEvent.keyDown(screen.queryByTestId('side-drawer'), {
        key: 'Escape',
        code: 'Escape',
        keyCode: 27,
        charCode: 27,
      });
    });

    await waitFor(async () => {
      expect(screen.queryByTestId('side-drawer')).toBeNull();
    });
  });

  test('should handle Tab event', async () => {
    const content = <>content</>;
    const Wrapped = () => {
      const [open, setOpen] = useState(true);

      return (
        <SidePage
          anchor="right"
          open={open}
          setOpen={setOpen}
        >
          {content}
        </SidePage>
      );
    };
    render(<Wrapped />);
    expect(screen.queryByTestId('side-drawer')).not.toBeNull();

    await act(async () => {
      fireEvent.keyDown(screen.queryByTestId('side-drawer'), {
        key: 'Tab',
        code: 'Tab',
        keyCode: 9,
        charCode: 9,
      });
    });

    await waitFor(async () => {
      expect(screen.queryByTestId('side-drawer')).not.toBeNull();
    });
  });

  test('should handle Shift event', async () => {
    const content = <>content</>;
    const Wrapped = () => {
      const [open, setOpen] = useState(true);

      return (
        <SidePage
          anchor="right"
          open={open}
          setOpen={setOpen}
        >
          {content}
        </SidePage>
      );
    };
    render(<Wrapped />);
    expect(screen.queryByTestId('side-drawer')).not.toBeNull();

    await act(async () => {
      fireEvent.keyDown(screen.queryByTestId('side-drawer'), {
        key: 'Shift',
        code: 'Shift',
        keyCode: 16,
        charCode: 16,
      });
    });

    await waitFor(async () => {
      expect(screen.queryByTestId('side-drawer')).not.toBeNull();
    });
  });
});
