import { JsonViewer } from '@textea/json-viewer';
import React from 'react';
import { MerkleProof, VoteReceipt } from 'types/voting-app-types';

export type RecordKeysToDisplay = Pick<
  VoteReceipt,
  'event' | 'proposal' | 'votingPower' | 'voterStakingAddress' | 'status' | 'id' | 'votedAtSlot'
> & {
  voteProof: {
    transactionHash: MerkleProof['transactionHash'];
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
  votingPower: 'Voting Power',
  voterStakingAddress: 'Voter Staking Address',
  status: 'Status',
  votedAtSlot: 'Voted at Slot',
  voteProof: 'Vote Proof',
};

export const shortenString = (string: string, s: number, e: number) =>
  `${string.slice(0, s)}...${string.slice(string.length - e, string.length)}`;

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

export const voteItemDescriptionMap: Record<FieldsToDisplayArrayKeys, string> = {
  event: 'This field identifies the specific voting/polling event.',
  proposal: 'Identifies the answer selected for this question.',
  votingPower:
    "This refers to the connected wallet's weighted voting power determined by the amount of ADA staked prior to the event.",
  voterStakingAddress: 'The stake address associated with the Cardano wallet casting the vote.',
  status: 'The current status of your vote receipt based on the current assurance level.',
};

export const voteItemAdvancedDescriptionMap: Record<AdvancedFullFieldsToDisplayArrayKeys, string> = {
  id: 'This is a unique identifier associated with the vote submitted.',
  votedAtSlot: 'The time of the vote submission represented in Cardano blockchain epoch slots.',
  voteProof: 'This is required to verify a vote was included on-chain.',
};
