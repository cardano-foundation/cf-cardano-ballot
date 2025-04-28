export const callAll =
  (...fns: (((...args: unknown[]) => void) | undefined)[]) =>
  (...args: unknown[]) =>
    fns.forEach((fn) => fn && fn(...args));
