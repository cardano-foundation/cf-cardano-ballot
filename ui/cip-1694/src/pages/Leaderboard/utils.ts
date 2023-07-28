import { Proposal } from 'types/types';

export const proposalOptions: Proposal[] = ['yes', 'no', 'abstain'];
export const proposalColorsMap: Record<Proposal, string> = {
  yes: '#43E4B7',
  no: '#FFBC5C',
  abstain: '#1D439B',
};
