import '@testing-library/jest-dom';
import React from 'react';
import { render, screen, fireEvent, act, within } from '@testing-library/react';
import { ChoseExplorerSection } from '../ChoseExplorerSection';
import { EXPLORERS } from '../utils';

describe('ChoseExplorerSection:', () => {
  test('should render proper state', async () => {
    const setExplorerMock = jest.fn();

    await act(async () => {
      render(
        <ChoseExplorerSection
          setExplorer={setExplorerMock}
          explorer={EXPLORERS[1].url}
        />
      );
    });

    const explorerSection = screen.queryByTestId('chose-explorer-section');
    expect(explorerSection).toBeInTheDocument();

    const options = within(explorerSection).queryAllByTestId('chose-explorer-option-card');
    expect(options.length).toEqual(EXPLORERS.length);
    for (const option in options) {
      expect(options[option].textContent).toEqual(EXPLORERS[option].label);
    }

    await act(async () => {
      fireEvent.click(options[0]);
    });

    expect(setExplorerMock).toBeCalledWith(EXPLORERS[0].url);
    expect(screen.queryAllByRole('button', { pressed: true })[0].textContent).toEqual(EXPLORERS[1].label);
  });
});
