import { ChainTip } from 'types/voting-ledger-follower-types';
import { Problem, SignedWeb3Request, Vote, VoteReceipt } from 'types/voting-app-types';
import { DEFAULT_CONTENT_TYPE_HEADERS, doRequest, HttpMethods } from '../handlers/httpHandler';
import {env} from 'common/constants/env';

export const CAST_VOTE_URL = `${env.VOTING_APP_SERVER_URL}/api/vote/cast`;
export const VOTE_RECEIPT_URL = `${env.VOTING_APP_SERVER_URL}/api/vote/receipt`;
export const BLOCKCHAIN_TIP_URL = `${env.VOTING_LEDGER_FOLLOWER_APP_SERVER_URL}/api/blockchain/tip`;

const castAVoteWithDigitalSignature = async (jsonRequest: SignedWeb3Request) =>
  await doRequest<Problem | Vote>(
    HttpMethods.POST,
    CAST_VOTE_URL,
    DEFAULT_CONTENT_TYPE_HEADERS,
    JSON.stringify(jsonRequest)
  );

const getSlotNumber = async () => {
  return await doRequest<ChainTip>(HttpMethods.GET, BLOCKCHAIN_TIP_URL, DEFAULT_CONTENT_TYPE_HEADERS);
};

const getVoteReceipt = async (jsonRequest: SignedWeb3Request) => {
  return await doRequest<Problem | VoteReceipt>(
    HttpMethods.POST,
    VOTE_RECEIPT_URL,
    DEFAULT_CONTENT_TYPE_HEADERS,
    JSON.stringify(jsonRequest)
  );
};


export {
  castAVoteWithDigitalSignature,
  getSlotNumber,
  getVoteReceipt
}