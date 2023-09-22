import { ChainTip } from 'types/voting-ledger-follower-types';
import { Problem, SignedWeb3Request, Vote, VoteReceipt, UserVotes } from 'types/voting-app-types';
import { DEFAULT_CONTENT_TYPE_HEADERS, doRequest, HttpMethods } from '../handlers/httpHandler';
import { env } from 'common/constants/env';
import { canonicalize } from 'json-canonicalize';

export const CAST_VOTE_URL = `${env.VOTING_APP_SERVER_URL}/api/vote/cast`;
export const VOTE_RECEIPT_URL = `${env.VOTING_APP_SERVER_URL}/api/vote/receipt`;
export const BLOCKCHAIN_TIP_URL = `${env.VOTING_LEDGER_FOLLOWER_APP_SERVER_URL}/api/blockchain/tip`;
export const USER_VOTES_URL = `${env.VOTING_APP_SERVER_URL}/api/vote/votes`;

type voteInput = {
  voteId: string;
  proposalId: string;
  categoryId: string;
  stakeAddress: string;
  slotNumber: string;
};

export const buildCanonicalVoteInputJson = ({
  voteId,
  categoryId,
  proposalId,
  stakeAddress,
  slotNumber,
}: voteInput): ReturnType<typeof canonicalize> => {
  const startOfCurrentDay = new Date();
  startOfCurrentDay.setUTCMinutes(0, 0, 0);
  return canonicalize({
    action: 'CAST_VOTE',
    actionText: 'Cast Vote',
    slot: slotNumber,
    data: {
      id: voteId,
      address: stakeAddress,
      event: env.EVENT_ID,
      category: categoryId,
      proposal: proposalId,
      network: env.TARGET_NETWORK,
      votedAt: slotNumber,
      votingEventType: 'USER_BASED',
    },
  });
};

const castAVoteWithDigitalSignature = async (jsonRequest: SignedWeb3Request) =>
  await doRequest<Problem | Vote>(
    HttpMethods.POST,
    CAST_VOTE_URL,
    DEFAULT_CONTENT_TYPE_HEADERS,
    JSON.stringify(jsonRequest),
    undefined,
    true
  );

const getSlotNumber = async () => {
  return await doRequest<ChainTip>(HttpMethods.GET, BLOCKCHAIN_TIP_URL, DEFAULT_CONTENT_TYPE_HEADERS);
};

const getVoteReceipt = async (categoryId: string, token: string) =>
  await doRequest<VoteReceipt>(
    HttpMethods.GET,
    `${VOTE_RECEIPT_URL}/${env.EVENT_ID}/${categoryId}`,
    {
      ...DEFAULT_CONTENT_TYPE_HEADERS,
    },
    null,
    token
  );

const getUserVotes = async (token: string) =>
  await doRequest<UserVotes[]>(
    HttpMethods.GET,
    `${USER_VOTES_URL}/${env.EVENT_ID}`,
    {
      ...DEFAULT_CONTENT_TYPE_HEADERS,
    },
    null,
    token
  );

export { castAVoteWithDigitalSignature, getSlotNumber, getVoteReceipt, getUserVotes };
