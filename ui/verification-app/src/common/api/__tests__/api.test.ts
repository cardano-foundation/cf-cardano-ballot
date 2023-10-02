const fetchMock = jest.fn();
const VOTING_VERIFICATION_APP_SERVER_URL = 'VOTING_VERIFICATION_APP_SERVER_URL';
import { MediaTypes } from 'common/handlers/httpHandler';
import { voteProofMock } from 'test/mocks';
import { verifyVote } from '../verificationService';

Object.defineProperty(window, 'fetch', {
  value: fetchMock,
});

jest.mock('../../../env', () => {
  const original = jest.requireActual('../../../env');
  return {
    ...original,
    env: {
      ...original.env,
      VOTING_VERIFICATION_APP_SERVER_URL,
    },
  };
});

describe('Testing services:', () => {
  afterEach(() => {
    jest.clearAllMocks();
  });
  test('Verification service', async () => {
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
    const { rootHash = '', steps = [], coseSignature, cosePublicKey } = voteProofMock;
    const payload = {
      rootHash,
      voteCoseSignature: coseSignature,
      voteCosePublicKey: cosePublicKey,
      steps,
    };
    expect(await verifyVote(payload)).toEqual(parsedResponse);
    expect(fetchMock).toBeCalledWith(`${VOTING_VERIFICATION_APP_SERVER_URL}/api/verification/verify-vote`, {
      body: JSON.stringify(payload),
      headers: { 'Content-Type': 'application/json;charset=UTF-8' },
      method: 'POST',
    });
  });
});
