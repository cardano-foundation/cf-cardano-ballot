import { renderHook, waitFor, act } from '@testing-library/react';
import { useToggle } from '../useToggle';

describe('useToggle:', () => {
  test('should toggle', async () => {
    const hook = renderHook(() => useToggle(true));

    expect(hook.result.current[0]).toBeTruthy();
    act(() => {
      hook.result.current[1]();
    });
    await waitFor(() => {
      expect(hook.result.current[0]).toBeFalsy();
    });
  });
});
