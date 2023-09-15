/* eslint-disable no-var */
var mockv4 = jest.fn();
import { accountDataMock, chainTipMock } from 'test/mocks';
import { buildCanonicalVoteInputJson } from '../voteUtils';

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
      '{"action":"CAST_VOTE","actionText":"Cast Vote","data":{"address":"stake_test1uqwcz0754wwpuhm6xhdpda6u9enyahaj5ynlc9ay5l4mlms4pyqyg","category":"CHANGE_GOV_STRUCTURE","event":"CIP-1694_Pre_Ratification_3316","id":"mockv4","network":"PREVIEW","proposal":"YES","votedAt":"36004360","votingPower":"9997463457"},"slot":"36004360","uri":"https://evoting.cardano.org/voltaire"}'
    );
  });

  test.todo('getSignedMessagePromise');
});
