export function wait(timeMs = 5000): Promise<boolean> {
  return new Promise((resolve) => {
    setTimeout(() => resolve(true), timeMs);
  });
}
