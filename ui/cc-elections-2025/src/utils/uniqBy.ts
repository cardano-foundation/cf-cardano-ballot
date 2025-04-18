/**
 * Returns an array of unique elements from the input array based on the specified key.
 * @param arr - The input array.
 * @param key - The key to determine uniqueness.
 * @returns An array of unique elements.
 * @template T - The type of elements in the array.
 */
export const uniqBy = <T>(arr: T[], key: keyof T): T[] => {
  const map = new Map<T[keyof T], T>();
  arr.forEach((item) => {
    map.set(item[key], item);
  });
  return Array.from(map.values());
};
