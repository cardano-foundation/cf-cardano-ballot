export const testIdFromLabel = (label: string) =>
  label?.trim().replace(/ /g, "-").toLocaleLowerCase();
