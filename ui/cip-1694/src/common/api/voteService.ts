import { DEFAULT_CONTENT_TYPE_HEADERS, doRequest, HttpMethods } from '../handlers/httpHandler';
import { env } from '../../env';
import {
  Problem,
  SignedWeb3Request,
  Vote,
  Event,
  ChainTip,
  Account,
  VoteReceipt,
} from '../../types/backend-services-types';

export const castAVoteWithDigitalSignature = async (jsonRequest: SignedWeb3Request) =>
  await doRequest<Problem | Vote>(
    HttpMethods.POST,
    env.CAST_VOTE_URL,
    DEFAULT_CONTENT_TYPE_HEADERS,
    JSON.stringify(jsonRequest)
  );

export const getSlotNumber = async () => {
  return await doRequest<ChainTip>(HttpMethods.GET, env.BLOCKCHAIN_TIP_URL, DEFAULT_CONTENT_TYPE_HEADERS);
};

export const getVoteReceipt = async (jsonRequest: SignedWeb3Request) => {
  return await doRequest<Problem | VoteReceipt>(
    HttpMethods.POST,
    env.VOTE_RECEIPT_URL,
    DEFAULT_CONTENT_TYPE_HEADERS,
    JSON.stringify(jsonRequest)
  );
};

export const getVotingPower = async (eventId: Event['id'], stakeAddress: string) => {
  return await doRequest<Account>(
    HttpMethods.GET,
    `${env.VOTING_POWER_URL}/${eventId}/${stakeAddress}`,
    DEFAULT_CONTENT_TYPE_HEADERS
  );
};
