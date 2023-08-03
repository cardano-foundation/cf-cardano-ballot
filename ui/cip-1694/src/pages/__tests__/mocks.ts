import { VoteReceipt } from 'types/backend-services-types';

export const VoteReceiptBasic: VoteReceipt = {
  id: 'e51fdf09...4c836052b4f0',
  event: 'CIP-1694_Pre_Ratifictaion-4619',
  proposal: 'YES',
  votingPower: '9,997k ADA',
  voterStakingAddress: 'stake1234567890pyqyg',
  status: 'BASIC',
  votedAtSlot: '33059418',
  merkleProof: {
    transactionHash: '',
    rootHash: '51861e99048e9beec0689fcafed7b3a13760472d8073d0b153edc265e2cb8d2',
    steps: [
      { type: 'Left', hash: '252f10c83610ebca1a059c0bae8255eba2f95be4d1d7bcfa89d7248a82d9f111' },
      { type: 'Right', hash: '6b7ba3e2b42d464f8b7dd6e74e23bbd75de48087bf33959d5cad25d240332c8c' },
      { type: 'Left', hash: '2a3c8dd1eef04d53583e7953ffdb8596bcef8b3b74fe54a599cbaa4954984473' },
    ],
  },
  coseSignature:
    '8458200201276761646472657373581de01d813fdab9cle5f7a35da16f75c2664edfb2a127c17a4a7ebbfeel6668617368656445901907622616374696f6e223a22434153545f596179356c346d6c6d73347079717967222c226361746567672792230224349502d3136393455072655526174696669636174696f6e534363139222c226576656e74223a224349502f73616c223a22594553222c227667465644174223a33323936333037392c227667469667506776572223a2239393937227d2c22736c674223a33323936333037392c2275726922375d8Fb996a6cb01',
  cosePublicKey: '04010103272006215820c4821499cef96eda9c00cdd',
  category: '',
};

export const VoteReceiptPartial: VoteReceipt = {
  ...VoteReceiptBasic,
  status: 'PARTIAL',
};

export const VoteReceiptRollback: VoteReceipt = {
  ...VoteReceiptBasic,
  status: 'ROLLBACK',
};

export const VoteReceiptFull_LowAssurance: VoteReceipt = {
  ...VoteReceiptBasic,
  status: 'FULL',
  finalityScore: 'LOW',
};

export const VoteReceiptFull_MediumAssurance: VoteReceipt = {
  ...VoteReceiptBasic,
  status: 'FULL',
  finalityScore: 'MEDIUM',
};

export const VoteReceiptFull_HighAssurance: VoteReceipt = {
  ...VoteReceiptBasic,
  status: 'FULL',
  finalityScore: 'VERY_HIGH',
};
