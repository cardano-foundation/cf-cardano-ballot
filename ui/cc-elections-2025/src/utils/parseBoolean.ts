/**
 * Parses a string value and returns its boolean equivalent.
 *
 * @param value - The string value to be parsed.
 * @returns `true` if the value is "true" (case insensitive),
 *          `false` if the value is "false" (case insensitive),
 *          or `null` if the value is neither.
 */
export const parseBoolean = (value: string): boolean | null =>
  ({
    true: true,
    false: false,
  }[String(value).toLowerCase()] ?? null);
