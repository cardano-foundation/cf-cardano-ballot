import { VoteReceipt, ByProposalsInCategoryStats } from 'types/voting-app-types';
import { EventPresentation, ChainTip, Account } from 'types/voting-ledger-follower-types';

import { useCardano } from '@cardano-foundation/cardano-connect-with-wallet';
import { canonicalize } from 'json-canonicalize';

export const canonicalVoteInputJsonMock = canonicalize({
  action: 'CAST_VOTE',
  actionText: 'Cast Vote',
  data: {
    address: 'stake_test1uqwcz0754wwpuhm6xhdpda6u9enyahaj5ynlc9ay5l4mlms4pyqyg',
    category: 'CHANGE_GOV_STRUCTURE',
    event: 'CIP-1694_Pre_Ratification_3316',
    id: 'ebff2758-7122-4007-899f-90eea0e236c0',
    network: 'PREPROD',
    proposal: 'YES',
    votedAt: '36316814',
    votingPower: '9997463457',
  },
  slot: '36316814',
  uri: 'https://evoting.cardano.org/voltaire',
});

export const accountDataMock: Account = {
  stakeAddress: 'stake_test1uqwcz0754wwpuhm6xhdpda6u9enyahaj5ynlc9ay5l4mlms4pyqyg',
  votingPower: '9997463457',
  votingPowerAsset: 'ADA',
  network: 'PREPROD',
  epochNo: 1,
};

export const chainTipMock: ChainTip = {
  synced: true,
  absoluteSlot: 36004360,
  epochNo: 87,
  hash: '22f30715e455ba4eb9d1333239c3508fae8f7c0c00a74859d3a55d26cd0e7289',
  network: 'PREPROD',
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

export const eventMock_active: EventPresentation = {
  started: true,
  commitmentsWindowOpen: true,
  id: 'CIP-1694_Pre_Ratification_3316',
  organisers: 'CF and IOG',
  votingEventType: 'STAKE_BASED',
  startSlot: null,
  endSlot: null,
  proposalsRevealSlot: null,
  startEpoch: 94,
  eventStartDate: '2023-09-14T00:00:00Z' as unknown as Date,
  eventEndDate: '2023-10-18T23:59:59Z' as unknown as Date,
  proposalsRevealDate: '2023-11-08T00:00:00Z' as unknown as Date,
  snapshotTime: '2023-09-13T23:59:59Z' as unknown as Date,
  endEpoch: 100,
  snapshotEpoch: 93,
  proposalsRevealEpoch: 105,
  categories: [
    {
      id: 'CHANGE_GOV_STRUCTURE',
      gdprProtection: false,
      proposals: [
        {
          id: '1f082124-ee46-4deb-9140-84a4529f98be',
          name: 'YES',
        },
        {
          id: 'ed9f03e8-8ee9-4de5-93a3-30779216f150',
          name: 'NO',
        },
      ],
    },
    {
      id: 'MIN_VIABLE_GOV_STRUCTURE',
      gdprProtection: false,
      proposals: [
        {
          id: '291f91b3-3e3c-402e-aebf-854f141b372b',
          name: 'CIP-1694',
        },
        {
          id: '842cf5fc-2eda-44a0-b067-87e6a7035aa1',
          name: 'OTHER',
        },
        {
          id: 'adcec241-67de-4860-a881-aaa91a5283a2',
          name: 'ABSTAIN',
        },
      ],
    },
  ],
  active: true,
  finished: false,
  notStarted: false,
  proposalsReveal: false,
  allowVoteChanging: false,
  highLevelEventResultsWhileVoting: false,
  highLevelCategoryResultsWhileVoting: false,
  categoryResultsWhileVoting: false,
};

export const eventMock_notStarted: EventPresentation = {
  ...eventMock_active,
  active: true,
  notStarted: true,
  finished: false,
};

export const eventMock_finished: EventPresentation = {
  ...eventMock_active,
  active: false,
  finished: true,
};

export const VoteReceiptMock_Basic: VoteReceipt = {
  id: 'e51fdf09asdasdasdasdasd4c836052b4f0',
  event: 'CIP-1694_Pre_Ratifictaion-4619',
  proposal: 'YES',
  votingPower: '9,997k ADA',
  voterStakingAddress: 'stake1234567890pyqyg',
  status: 'BASIC',
  votedAtSlot: '33059418',
  merkleProof: {
    transactionHash: '189f72d07182e89520ee040f49d884a7c68e21a6e6511dc0d1916940bc016420',
    rootHash: '51861e99048e9beec0689fcafed7b3a13760472d8073d0b153edc265e2cb8d2',
    steps: [
      { type: 'L', hash: '252f10c83610ebca1a059c0bae8255eba2f95be4d1d7bcfa89d7248a82d9f111' },
      { type: 'R', hash: '6b7ba3e2b42d464f8b7dd6e74e23bbd75de48087bf33959d5cad25d240332c8c' },
      { type: 'L', hash: '2a3c8dd1eef04d53583e7953ffdb8596bcef8b3b74fe54a599cbaa4954984473' },
    ],
  },
  coseSignature:
    '8458200201276761646472657373581de01d813fdab9cle5f7a35da16f75c2664edfb2a127c17a4a7ebbfeel6668617368656445901907622616374696f6e223a22434153545f596179356c346d6c6d73347079717967222c226361746567672792230224349502d3136393455072655526174696669636174696f6e534363139222c226576656e74223a224349502f73616c223a22594553222c227667465644174223a33323936333037392c227667469667506776572223a2239393937227d2c22736c674223a33323936333037392c2275726922375d8Fb996a6cb01',
  cosePublicKey: '04010103272006215820c4821499cef96eda9c00cdd',
  category: 'CHANGE_GOV_STRUCTURE',
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

export const userInSessionMock = {
  accessToken:
    'eyJhbGciOiJFZERTQSJ9.eyJzdWIiOiJzdGFrZV90ZXN0MXVxd2N6MDc1NHd3cHVobTZ4aGRwZGE2dTllbnlhaGFqNXlubGM5YXk1bDRtbG1zNHB5cXlnIiwiZXZlbnRJZCI6IkNJUC0xNjk0X1ByZV9SYXRpZmljYXRpb25fMzMxNiIsInJvbGUiOiJWT1RFUiIsImlzcyI6Imh0dHBzOi8vY2FyZGFub2ZvdW5kYXRpb24ub3JnIiwic3Rha2VBZGRyZXNzIjoic3Rha2VfdGVzdDF1cXdjejA3NTR3d3B1aG02eGhkcGRhNnU5ZW55YWhhajV5bmxjOWF5NWw0bWxtczRweXF5ZyIsImV4cCI6MTY5NDc4NDk4NCwiaWF0IjoxNjk0Njk4NTg0LCJqdGkiOiIzNmIxZjc1NS1mZDc2LTQyMzAtYTVmMy0zYjhkMDJhN2I2ZGYiLCJjYXJkYW5vTmV0d29yayI6IlBSRVBST0QifQ.MHXhEiXhak-5HOVxBRN9y5kx5LGO2zIpU3c4L09GNlg8cJDqtfSgFwgDl0eY0kZQQKkWJhT5kpz5V7Bqu7fxDQ',
  expiresAt: '2023-09-15T16:36:24.903634',
};

export const voteStats: ByProposalsInCategoryStats = {
  category: 'CHANGE_GOV_STRUCTURE',
  proposals: {
    [eventMock_finished.categories[0].proposals[0].id]: { votes: 2134123, votingPower: '91000000000000' },
    [eventMock_finished.categories[0].proposals[1].id]: { votes: 70011, votingPower: '1000000000000000' },
  },
};
