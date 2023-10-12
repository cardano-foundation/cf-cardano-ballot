import { CategoryPresentation } from 'types/voting-ledger-follower-types';

export const categoryColorsMap: Record<CategoryPresentation['id'], string> = {
  AMBASSADOR: '#106593',
  BLOCKCHAIN_FOR_GOOD: '#FFBC5C',
  CIPS: '#75FA9F',
  BEST_DEFI_DEX: '#652701',
  BEST_DEVELOPER_OR_DEVELOPER_TOOLS: '#FD873C',
  EDUCATIONAL_INFLUENCER: '#1894D6',
  MARKETPLACE: '#C20024',
  MOST_IMPACTFUL_SSPO: '#056122',
  NFT_PROJECT: '#ad0ec5',
  SSI: '#DAEEFB',
};

export const getPercentage = (value: number, total: number) => (value * 100) / total;

export const formatUTCDate = (date: string) => {
  if (!date) return '';
  const isoDate = new Date(date).toISOString();
  return `${isoDate.substring(0, 4)} ${isoDate.substring(11, 16)} UTC`;
};
