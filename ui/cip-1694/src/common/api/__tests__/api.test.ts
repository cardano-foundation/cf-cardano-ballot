const fetchMock = jest.fn();
const mockCanonicalize = jest.fn();
const EVENT_ID = 'CIP-1694_Pre_Ratification_3316';
const TARGET_NETWORK = 'Preprod';
const VOTING_LEDGER_FOLLOWER_APP_SERVER_URL = 'VOTING_LEDGER_FOLLOWER_APP_SERVER_URL';
const VOTING_APP_SERVER_URL = 'VOTING_APP_SERVER_URL';
const CATEGORY_ID = 'CHANGE_GOV_STRUCTURE';
import { MediaTypes } from 'common/handlers/httpHandler';
import { getStats } from '../leaderboardService';
import { buildCanonicalLoginJson, submitLogin } from '../loginService';
import { getEvent } from '../referenceDataService';
import { castAVoteWithDigitalSignature, getChainTip, getVoteReceipt, getVotingPower } from '../voteService';

Object.defineProperty(window, 'fetch', {
  value: fetchMock,
});

jest.mock('../../../env', () => {
  const original = jest.requireActual('../../../env');
  return {
    ...original,
    env: {
      ...original.env,
      VOTING_APP_SERVER_URL,
      VOTING_LEDGER_FOLLOWER_APP_SERVER_URL,
      EVENT_ID,
      TARGET_NETWORK,
      CATEGORY_ID,
    },
  };
});

jest.mock('json-canonicalize', () => ({
  ...jest.requireActual('json-canonicalize'),
  canonicalize: mockCanonicalize,
}));

describe('Testing services:', () => {
  afterEach(() => {
    jest.clearAllMocks();
  });
  test('Leaderboard service', async () => {
    const parsedResponse = 'parsedResponse';
    const json = jest.fn(() => parsedResponse);
    fetchMock.mockReset();
    fetchMock.mockImplementation(
      async () =>
        await {
          headers: { get: () => MediaTypes.APPLICATION_JSON },
          status: 200,
          json,
        }
    );
    const categoryId = 'categoryId';
    expect(await getStats(categoryId)).toEqual(parsedResponse);
    expect(fetchMock).toBeCalledWith(`${VOTING_APP_SERVER_URL}/api/leaderboard/${EVENT_ID}/categoryId`, {
      headers: { 'Content-Type': 'application/json;charset=UTF-8' },
      method: 'GET',
    });
  });

  describe('login service', () => {
    test('buildCanonicalLoginJson', () => {
      const slotNumber = 'slotNumber';
      const stakeAddress = 'stakeAddress';
      const canonicalLoginJson = 'canonicalLoginJson';
      mockCanonicalize.mockReset();
      mockCanonicalize.mockReturnValue(canonicalLoginJson);
      expect(buildCanonicalLoginJson({ stakeAddress, slotNumber })).toEqual(canonicalLoginJson);
      expect(mockCanonicalize).toBeCalledWith({
        action: 'LOGIN',
        actionText: 'Login',
        slot: slotNumber,
        data: {
          address: stakeAddress,
          event: EVENT_ID,
          network: TARGET_NETWORK,
          role: 'VOTER',
        },
      });
    });
    test('submitLogin', async () => {
      const parsedResponse = 'parsedResponse';
      const json = jest.fn(() => parsedResponse);
      fetchMock.mockReset();
      fetchMock.mockImplementation(
        async () =>
          await {
            headers: { get: () => MediaTypes.APPLICATION_JSON },
            status: 200,
            json,
          }
      );
      const jsonRequest = {
        coseSignature: 'coseSignature',
        cosePublicKey: 'cosePublicKey',
      };
      expect(await submitLogin(jsonRequest)).toEqual(parsedResponse);
      expect(fetchMock).toBeCalledWith(`${VOTING_APP_SERVER_URL}/api/auth/login`, {
        headers: {
          'Content-Type': 'application/json;charset=UTF-8',
          'X-CIP93-Public-Key': 'cosePublicKey',
          'X-CIP93-Signature': 'coseSignature',
        },
        method: 'GET',
      });
    });
  });

  test('Reference service', async () => {
    const parsedResponse = 'parsedResponse';
    const json = jest.fn(() => parsedResponse);
    fetchMock.mockReset();
    fetchMock.mockImplementation(
      async () =>
        await {
          headers: { get: () => MediaTypes.APPLICATION_JSON },
          status: 200,
          json,
        }
    );
    expect(await getEvent(EVENT_ID)).toEqual(parsedResponse);
    expect(fetchMock).toBeCalledWith(`${VOTING_LEDGER_FOLLOWER_APP_SERVER_URL}/api/reference/event/${EVENT_ID}`, {
      headers: { 'Content-Type': 'application/json;charset=UTF-8' },
      method: 'GET',
    });
  });
  describe('Vote service', () => {
    test('castAVoteWithDigitalSignature', async () => {
      const parsedResponse = 'parsedResponse';
      const json = jest.fn(() => parsedResponse);
      fetchMock.mockReset();
      fetchMock.mockImplementation(
        async () =>
          await {
            headers: { get: () => MediaTypes.APPLICATION_JSON },
            status: 200,
            json,
          }
      );
      const jsonRequest = {
        coseSignature: 'coseSignature',
        cosePublicKey: 'cosePublicKey',
      };
      expect(await castAVoteWithDigitalSignature(jsonRequest)).toEqual(parsedResponse);
      expect(fetchMock).toBeCalledWith(`${VOTING_APP_SERVER_URL}/api/vote/cast`, {
        headers: {
          'Content-Type': 'application/json;charset=UTF-8',
          'X-CIP93-Public-Key': 'cosePublicKey',
          'X-CIP93-Signature': 'coseSignature',
        },
        method: 'POST',
      });
    });

    test('getChainTip', async () => {
      const parsedResponse = 'parsedResponse';
      const json = jest.fn(() => parsedResponse);
      fetchMock.mockReset();
      fetchMock.mockImplementation(
        async () =>
          await {
            headers: { get: () => MediaTypes.APPLICATION_JSON },
            status: 200,
            json,
          }
      );
      expect(await getChainTip()).toEqual(parsedResponse);
      expect(fetchMock).toBeCalledWith(`${VOTING_LEDGER_FOLLOWER_APP_SERVER_URL}/api/blockchain/tip`, {
        headers: {
          'Content-Type': 'application/json;charset=UTF-8',
        },
        method: 'GET',
      });
    });

    test('getVoteReceipt', async () => {
      const token = 'token';
      const parsedResponse = 'parsedResponse';
      const json = jest.fn(() => parsedResponse);
      fetchMock.mockReset();
      fetchMock.mockImplementation(
        async () =>
          await {
            headers: { get: () => MediaTypes.APPLICATION_JSON },
            status: 200,
            json,
          }
      );
      expect(await getVoteReceipt(CATEGORY_ID, token)).toEqual(parsedResponse);
      expect(fetchMock).toBeCalledWith(`${VOTING_APP_SERVER_URL}/api/vote/receipt/${EVENT_ID}/${CATEGORY_ID}`, {
        headers: {
          Authorization: 'Bearer token',
          'Content-Type': 'application/json;charset=UTF-8',
        },
        method: 'GET',
      });
    });

    test('getVotingPower', async () => {
      const stakeAddress = 'stakeAddress';
      const parsedResponse = 'parsedResponse';
      const json = jest.fn(() => parsedResponse);
      fetchMock.mockReset();
      fetchMock.mockImplementation(
        async () =>
          await {
            headers: { get: () => MediaTypes.APPLICATION_JSON },
            status: 200,
            json,
          }
      );
      expect(await getVotingPower(EVENT_ID, stakeAddress)).toEqual(parsedResponse);
      expect(fetchMock).toBeCalledWith(
        `${VOTING_LEDGER_FOLLOWER_APP_SERVER_URL}/api/account/${EVENT_ID}/${stakeAddress}`,
        {
          headers: {
            'Content-Type': 'application/json;charset=UTF-8',
          },
          method: 'GET',
        }
      );
    });
  });
});
