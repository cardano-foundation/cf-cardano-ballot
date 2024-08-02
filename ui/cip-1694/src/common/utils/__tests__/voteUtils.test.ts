/* eslint-disable no-var */
var mockv4 = jest.fn();
import { accountDataMock, chainTipMock } from 'test/mocks';
import { buildCanonicalVoteInputJson, getSignedMessagePromise } from '../voteUtils';

jest.mock('uuid', () => ({
  v4: mockv4,
}));

jest.mock('../../../env', () => {
  const original = jest.requireActual('../../../env');
  return {
    ...original,
    env: {
      ...original.env,
      CATEGORY_ID: 'CHANGE_GOV_STRUCTURE',
      EVENT_ID: 'CIP-1694_Pre_Ratification_3316',
      TARGET_NETWORK: 'Preprod',
      ENV_URI: 'ENV_URI',
    },
  };
});

describe('voteUtils: ', () => {
  const mockv4Value = 'mockv4';
  mockv4.mockReturnValue(mockv4Value);

  beforeEach(() => {
    jest.clearAllMocks();
  });
  test('buildCanonicalVoteInputJson', () => {
    expect(
      buildCanonicalVoteInputJson({
        option: 'YES',
        voter: accountDataMock.stakeAddress,
        voteId: mockv4Value,
        slotNumber: chainTipMock.absoluteSlot.toString(),
        votingPower: accountDataMock.votingPower,
        category: 'CHANGE_GOV_STRUCTURE',
        uri: 'ENV_URI',
      })
    ).toEqual(
      '{"action":"CAST_VOTE","actionText":"Cast Vote","data":{"address":"stake_test1uqwcz0754wwpuhm6xhdpda6u9enyahaj5ynlc9ay5l4mlms4pyqyg","category":"CHANGE_GOV_STRUCTURE","event":"CIP-1694_Pre_Ratification_3316","id":"mockv4","network":"Preprod","proposal":"YES","votedAt":"36004360","votingPower":"9997463457"},"slot":"36004360","uri":"ENV_URI"}'
    );
  });

  test('getSignedMessagePromise', async () => {
    const signature = 'signature';
    const key = 'key';
    const message = 'message';

    expect(
      await getSignedMessagePromise(
        jest.fn(async (_message, sucessCb) => {
          await sucessCb(signature, key);
        })
      )(message)
    ).toEqual({
      coseSignature: signature,
      cosePublicKey: key,
    });

    expect(
      await getSignedMessagePromise(
        jest.fn(async (_message, sucessCb) => {
          await sucessCb(signature, null);
        })
      )(message)
    ).toEqual({
      coseSignature: signature,
      cosePublicKey: '',
    });

    const error = 'error';
    try {
      await getSignedMessagePromise(
        jest.fn(async (_message, _sucessCb, onErrorCb) => {
          await onErrorCb(error as unknown as Error);
        })
      )(message);
    } catch (_error) {
      expect(_error).toEqual(error);
    }
  });
});
