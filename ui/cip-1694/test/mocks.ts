import { VoteReceipt, EventReference, ChainTip } from 'types/backend-services-types';
import { useCardano } from '@cardano-foundation/cardano-connect-with-wallet';

export const chainTipMock: ChainTip = {
  absoluteSlot: 36004360,
  epochNo: 87,
  hash: '22f30715e455ba4eb9d1333239c3508fae8f7c0c00a74859d3a55d26cd0e7289',
};

export const useCardanoMock: ReturnType<typeof useCardano> = {
  isEnabled: true,
  isConnected: true,
  isConnecting: false,
  enabledWallet: 'flint',
  stakeAddress: 'stake_test1uqwcz0754wwpuhm6xhdpda6u9enyahaj5ynlc9ay5l4mlms4pyqyg',
  usedAddresses: [
    'addr_test1qppcuzs25ghufz2mzg37hcm77px4matwz2hz072z4ms7umqasylaf2uure0h5dw6zmm4ctnxfm0m9gf8lst6fflthlhq5hrg7w',
  ],
  unusedAddresses: [
    'addr_test1qppcuzs25ghufz2mzg37hcm77px4matwz2hz072z4ms7umqasylaf2uure0h5dw6zmm4ctnxfm0m9gf8lst6fflthlhq5hrg7w',
  ],
  signMessage: () =>
    // message: string,
    // onSignMessage?: ((signature: string, key: string | undefined) => void) | undefined,
    // onSignError?: ((error: Error) => void) | undefined
    Promise.resolve(),
  connect: () =>
    // walletName: string,
    // onConnect?: () => void | undefined,
    // onError?: ((code: Error) => void) | undefined
    Promise.resolve(),
  // eslint-disable-next-line @typescript-eslint/no-empty-function
  disconnect: () => {},
  installedExtensions: ['lace', 'flint'],
  accountBalance: 9984.272506,
};

export const useCardanoMock_notConnected: ReturnType<typeof useCardano> = {
  ...useCardanoMock,
  isConnected: false,
  enabledWallet: null,
};

export const eventMock_active: EventReference = {
  active: true,
  categories: [
    {
      gdprProtection: false,
      id: 'CIP-1694_Pre_Ratification_4619',
      presentationName: 'CIP-1694 Pre-Ratification',
      proposals: [
        { id: '00048bb6-028d-4f13-b3e5-d19deb22d2c2', name: 'YES', presentationName: 'Yes' },
        { id: 'e858953c-37f2-4d1b-b844-c2e4b125fe23', name: 'NO', presentationName: 'No' },
        { id: '6f05012e-081e-4746-ba53-1833ff995fe3', name: 'ABSTAIN', presentationName: 'Abstain' },
      ],
    },
  ],
  endEpoch: 95,
  endSlot: null,
  eventEnd: '2023-09-23T23:59:59Z' as unknown as Date,
  eventStart: '2023-07-06T00:00:00Z' as unknown as Date,
  finished: false,
  id: 'CIP-1694_Pre_Ratification_4619',
  presentationName: 'CIP-1694 Pre-Ratification',
  snapshotEpoch: 79,
  snapshotTime: '2023-07-05T23:59:59Z' as unknown as Date,
  startEpoch: 80,
  startSlot: null,
  team: 'CF & IOG',
  votingEventType: 'STAKE_BASED',
};

export const eventMock_notStarted: EventReference = {
  ...eventMock_active,
  active: false,
  finished: false,
};

export const eventMock_finished: EventReference = {
  ...eventMock_active,
  active: false,
  finished: true,
};

export const VoteReceiptMock_Basic: VoteReceipt = {
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

export const VoteReceiptMock_Partial: VoteReceipt = {
  ...VoteReceiptMock_Basic,
  status: 'PARTIAL',
};

export const VoteReceiptMock_Rollback: VoteReceipt = {
  ...VoteReceiptMock_Basic,
  status: 'ROLLBACK',
};

export const VoteReceiptMock_Full_LowAssurance: VoteReceipt = {
  ...VoteReceiptMock_Basic,
  status: 'FULL',
  finalityScore: 'LOW',
};

export const VoteReceiptMock_Full_MediumAssurance: VoteReceipt = {
  ...VoteReceiptMock_Basic,
  status: 'FULL',
  finalityScore: 'MEDIUM',
};

export const VoteReceiptMock_Full_HighAssurance: VoteReceipt = {
  ...VoteReceiptMock_Basic,
  status: 'FULL',
  finalityScore: 'VERY_HIGH',
};
