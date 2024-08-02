import { ProposalPresentation } from 'types/voting-ledger-follower-types';

export const proposalColorsMap: Record<ProposalPresentation['name'], string> = {
  YES: '#43E4B7',
  NO: '#FFBC5C',
  ABSTAIN: '#1D439B',
};

export const formatNumber = (number: number | bigint) =>
  new Intl.NumberFormat('en-EN', { maximumFractionDigits: 3 }).format(number);

export const getPercentage = (value: number | string, total: number | string) =>
  parseFloat(formatNumber(Number((BigInt(value) * BigInt(100) * BigInt(100)) / BigInt(total)) / 100));

export const formatUTCDate = (date: string) => {
  if (!date) return '';
  const isoDate = new Date(date).toISOString();
  return `${isoDate.substring(0, 4)} ${isoDate.substring(11, 16)} UTC`;
};
