/* eslint-disable no-var */
var mockv4 = jest.fn();
import { accountDataMock, chainTipMock } from 'test/mocks';
import { buildCanonicalVoteInputJson, buildCanonicalVoteReceiptInputJson } from '../voteUtils';

jest.mock('uuid', () => ({
  v4: mockv4,
}));

jest.mock('../../../env', () => {
  const original = jest.requireActual('../../../env');
  return {
    ...original,
    env: {
      ...original.env,
      CATEGORY_ID: 'CIP-1694_Pre_Ratification_4619',
      EVENT_ID: 'CIP-1694_Pre_Ratification_4619',
      TARGET_NETWORK: 'PREVIEW',
    },
  };
});

describe('voteUtils: ', () => {
  const mockv4Value = 'mockv4';
  mockv4.mockReturnValue(mockv4Value);
  test('buildCanonicalVoteInputJson', () => {
    expect(
      buildCanonicalVoteInputJson({
        option: 'YES',
        voter: accountDataMock.stakeAddress,
        voteId: mockv4Value,
        slotNumber: chainTipMock.absoluteSlot.toString(),
        votePower: accountDataMock.votingPower,
      })
    ).toEqual(
      '{"action":"CAST_VOTE","actionText":"Cast Vote","data":{"address":"stake_test1uqwcz0754wwpuhm6xhdpda6u9enyahaj5ynlc9ay5l4mlms4pyqyg","category":"CIP-1694_Pre_Ratification_4619","event":"CIP-1694_Pre_Ratification_4619","id":"mockv4","network":"PREVIEW","proposal":"YES","votedAt":"36004360","votingPower":"9997463457"},"slot":"36004360","uri":"https://evoting.cardano.org/voltaire"}'
    );
  });
  test('buildCanonicalVoteReceiptInputJson', () => {
    expect(
      buildCanonicalVoteReceiptInputJson({
        voter: accountDataMock.stakeAddress,
        slotNumber: chainTipMock.absoluteSlot.toString(),
      })
    ).toEqual(
      '{"action":"VIEW_VOTE_RECEIPT","actionText":"View Vote Receipt","data":{"address":"stake_test1uqwcz0754wwpuhm6xhdpda6u9enyahaj5ynlc9ay5l4mlms4pyqyg","category":"CIP-1694_Pre_Ratification_4619","event":"CIP-1694_Pre_Ratification_4619","network":"PREVIEW"},"slot":"36004360","uri":"https://evoting.cardano.org/voltaire"}'
    );
  });

  test.todo('getSignedMessagePromise');
});
