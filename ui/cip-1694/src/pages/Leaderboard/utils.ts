import BigNumber from 'bignumber.js';
import { ProposalPresentation } from 'types/voting-ledger-follower-types';

const LOVELACE_VALUE = 1_000_000;
const DEFAULT_DECIMALS = 2;

export const proposalColorsMap: Record<ProposalPresentation['name'], string> = {
  YES: '#43E4B7',
  NO: '#FFBC5C',
  ABSTAIN: '#1D439B',
};

export const formatNumber = (number: number | string | BigNumber) => new BigNumber(number).toFormat();

export const getPercentage = (value: number | string, total: number | string) =>
  parseFloat(new BigNumber(value).times(100).dividedBy(total).toFixed(2));

export const formatUTCDate = (date: string) => {
  if (!date) return '';
  const isoDate = new Date(date).toISOString();
  return `${isoDate.substring(0, 4)} ${isoDate.substring(11, 16)} UTC`;
};

export const lovelacesToAdaString = (
  lovelaces: string | BigNumber | number,
  decimalValues: number = DEFAULT_DECIMALS
): string => `${new BigNumber(lovelaces).dividedBy(LOVELACE_VALUE).toFormat(decimalValues)} ₳`;
