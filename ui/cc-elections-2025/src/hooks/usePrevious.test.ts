import { renderHook } from "@testing-library/react";
import { describe, it, expect } from "vitest";
import usePrevious from "./usePrevious";

describe("usePrevious hook", () => {
  it("should return undefined on the initial render", () => {
    const { result } = renderHook(() => usePrevious(0));

    expect(result.current).toBeUndefined();
  });

  it("should return the previous value after the state changes", () => {
    let value = 0;

    const { result, rerender } = renderHook(() => usePrevious(value));

    expect(result.current).toBeUndefined();

    value = 10;
    rerender();

    expect(result.current).toBe(0);

    value = 20;
    rerender();

    expect(result.current).toBe(10);
  });

  it("should handle non-primitive values like objects", () => {
    let value = { count: 0 };

    const { result, rerender } = renderHook(() => usePrevious(value));

    expect(result.current).toBeUndefined();

    value = { count: 1 };
    rerender();

    expect(result.current).toEqual({ count: 0 });

    value = { count: 2 };
    rerender();

    expect(result.current).toEqual({ count: 1 });
  });
});
