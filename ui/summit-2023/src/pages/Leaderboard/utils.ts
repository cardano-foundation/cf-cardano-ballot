import { CategoryPresentation } from 'types/voting-ledger-follower-types';

export const categoryColorsMap: Record<CategoryPresentation['id'], string> = {
  BEST_WALLET: '#43E4B7',
  BEST_DEX: '#FFBC5C'
};

export const getPercentage = (value: number, total: number) => (value * 100) / total;

export const formatUTCDate = (date: string) => {
  if (!date) return '';
  const isoDate = new Date(date).toISOString();
  return `${isoDate.substring(0, 4)} ${isoDate.substring(11, 16)} UTC`;
};
