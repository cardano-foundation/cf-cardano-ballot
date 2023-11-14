import { ProposalPresentation } from 'types/voting-ledger-follower-types';

export const proposalColorsMap: Record<ProposalPresentation['name'], string> = {
  YES: '#43E4B7',
  NO: '#FFBC5C',
  ABSTAIN: '#1D439B',
};

export const getPercentage = (value: number, total: number) => (value * 100) / total;

export const formatUTCDate = (date: string) => {
  if (!date) return '';
  const isoDate = new Date(date).toISOString();
  return `${isoDate.substring(0, 4)} ${isoDate.substring(11, 16)} UTC`;
};