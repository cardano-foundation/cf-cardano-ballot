import { VoteReceipt } from 'types/backend-services-types';

export type RecordKeysToDisplay = Pick<
  VoteReceipt,
  | 'id'
  | 'event'
  | 'category'
  | 'proposal'
  | 'votingPower'
  | 'voterStakingAddress'
  | 'coseSignature'
  | 'cosePublicKey'
  | 'status'
  | 'votedAtSlot'
>;

export type FieldsToDisplayArrayKeys = keyof RecordKeysToDisplay;
export const fieldsToDisplay: FieldsToDisplayArrayKeys[] = [
  'id',
  'event',
  'category',
  'proposal',
  'votingPower',
  'voterStakingAddress',
  'coseSignature',
  'cosePublicKey',
  'status',
  'votedAtSlot',
];

export const labelTransformerMap: Record<FieldsToDisplayArrayKeys, string> = {
  id: 'ID',
  event: 'Event',
  category: 'Category',
  proposal: 'Proposal',
  votingPower: 'Voting power',
  voterStakingAddress: 'Voter staking address',
  coseSignature: 'Cose signature',
  cosePublicKey: 'Cose public key',
  status: 'Status',
  votedAtSlot: 'Voted at slot',
};

const shortenString = (string: string, s: number, e: number) =>
  `${string.slice(0, s)}...${string.slice(string.length - e, string.length - 1)}`;

export const valueTransformerMap: Partial<
  Record<FieldsToDisplayArrayKeys, (value: RecordKeysToDisplay[FieldsToDisplayArrayKeys]) => string>
> = {
  id: (value) => shortenString(value, 8, 12),
  voterStakingAddress: (value) => shortenString(value, 5, 5),
  cosePublicKey: (value) => shortenString(value, 5, 5),
};
