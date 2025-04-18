import { useEffect, useRef } from "react";

/**
 * Custom React hook to get the previous value of a prop or state.
 * @param value The current value to track.
 * @returns The previous value of the given input.
 */
function usePrevious<T>(value: T): T | undefined {
  const ref = useRef<T>();

  useEffect(() => {
    ref.current = value; // Update the ref value to the current value after render
  }, [value]); // Only run if the value changes

  return ref.current; // Return the previous value (ref.current holds the old value)
}

export default usePrevious;
