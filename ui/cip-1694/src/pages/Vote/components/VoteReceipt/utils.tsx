import { JsonViewer } from '@textea/json-viewer';
import React from 'react';
import { MerkleProof, VoteReceipt } from 'types/voting-app-types';

export type RecordKeysToDisplay = Pick<
  VoteReceipt,
  'event' | 'proposal' | 'votingPower' | 'voterStakingAddress' | 'status' | 'id' | 'votedAtSlot'
> & {
  voteProof: {
    steps: MerkleProof['steps'];
    rootHash: MerkleProof['rootHash'];
    coseSignature: VoteReceipt['coseSignature'];
    cosePublicKey: VoteReceipt['cosePublicKey'];
  };
};

export type FieldsToDisplayArrayKeys = keyof Pick<
  RecordKeysToDisplay,
  'event' | 'proposal' | 'votingPower' | 'voterStakingAddress' | 'status'
>;
export const generalFieldsToDisplay: FieldsToDisplayArrayKeys[] = [
  'event',
  'proposal',
  'votingPower',
  'voterStakingAddress',
  'status',
];

export type AdvancedFieldsToDisplayArrayKeys = keyof Pick<RecordKeysToDisplay, 'id' | 'votedAtSlot'>;
export const advancedFieldsToDisplay: AdvancedFieldsToDisplayArrayKeys[] = ['id', 'votedAtSlot'];

export type AdvancedFullFieldsToDisplayArrayKeys = keyof Pick<RecordKeysToDisplay, 'id' | 'votedAtSlot' | 'voteProof'>;
export const advancedFullFieldsToDisplay: AdvancedFullFieldsToDisplayArrayKeys[] = ['id', 'votedAtSlot', 'voteProof'];

export const labelTransformerMap: Record<FieldsToDisplayArrayKeys | AdvancedFullFieldsToDisplayArrayKeys, string> = {
  id: 'ID',
  event: 'Event',
  proposal: 'Proposal',
  votingPower: 'Voting power',
  voterStakingAddress: 'Voter staking address',
  status: 'Status',
  votedAtSlot: 'Voted at slot',
  voteProof: 'Vote Proof',
};

const shortenString = (string: string, s: number, e: number) =>
  `${string.slice(0, s)}...${string.slice(string.length - e, string.length - 1)}`;

export const valueTransformerMap: Partial<
  Record<
    FieldsToDisplayArrayKeys | AdvancedFullFieldsToDisplayArrayKeys,
    (
      value: RecordKeysToDisplay[FieldsToDisplayArrayKeys | AdvancedFullFieldsToDisplayArrayKeys]
    ) => string | React.ReactElement
  >
> = {
  id: (value: RecordKeysToDisplay['id']) => shortenString(value, 8, 12),
  voterStakingAddress: (value: RecordKeysToDisplay['voterStakingAddress']) => shortenString(value, 5, 5),
  voteProof: (value: RecordKeysToDisplay['voteProof']) => (
    <JsonViewer
      value={value}
      enableClipboard={false}
    />
  ),
};
