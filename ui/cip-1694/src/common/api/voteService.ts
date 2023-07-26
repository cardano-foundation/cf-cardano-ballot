import { DEFAULT_CONTENT_TYPE_HEADERS, doRequest, HttpMethods } from '../handlers/httpHandler';
import {
  EVENT_BY_ID_REFERENCE_URL,
  CAST_VOTE_URL,
  VOTE_RECEIPT_URL,
  BLOCKCHAIN_TIP_URL,
  VOTING_POWER_URL,
} from '../constants/appConstants';
import {
  Problem,
  SignedWeb3Request,
  Vote,
  Event,
  ChainTip,
  Account,
  VoteReceipt,
} from '../../types/backend-services-types';

export const getEventById = async (eventId: Event['id']) =>
  await doRequest<Vote>(HttpMethods.GET, `${EVENT_BY_ID_REFERENCE_URL}/${eventId}`, {
    ...DEFAULT_CONTENT_TYPE_HEADERS,
  });

export const castAVoteWithDigitalSignature = async (jsonRequest: SignedWeb3Request) =>
  await doRequest<Problem | Vote>(
    HttpMethods.POST,
    CAST_VOTE_URL,
    DEFAULT_CONTENT_TYPE_HEADERS,
    JSON.stringify(jsonRequest),
    false
  );

export const getSlotNumber = async () => {
  return await doRequest<ChainTip>(HttpMethods.GET, BLOCKCHAIN_TIP_URL, DEFAULT_CONTENT_TYPE_HEADERS);
};

export const getVoteReceipt = async (jsonRequest: SignedWeb3Request) => {
  return await doRequest<Problem | VoteReceipt>(
    HttpMethods.POST,
    VOTE_RECEIPT_URL,
    DEFAULT_CONTENT_TYPE_HEADERS,
    JSON.stringify(jsonRequest)
  );
};

export const getVotingPower = async (eventId: Event['id'], stakeAddress: string) => {
  return await doRequest<Account>(
    HttpMethods.GET,
    `${VOTING_POWER_URL}/${eventId}/${stakeAddress}`,
    DEFAULT_CONTENT_TYPE_HEADERS
  );
};
