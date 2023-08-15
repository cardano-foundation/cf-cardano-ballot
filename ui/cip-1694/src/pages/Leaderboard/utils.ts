import { ProposalReference } from 'types/backend-services-types';

export const proposalColorsMap: Record<ProposalReference['name'], string> = {
  yes: '#43E4B7',
  no: '#FFBC5C',
  abstain: '#1D439B',
};
