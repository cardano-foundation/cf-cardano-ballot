import queryString, { StringifiableRecord } from 'query-string';
import { EventPresentation, ChainTip, Account } from 'types/voting-ledger-follower-types';
import { SignedWeb3Request, Vote, VoteReceipt } from 'types/voting-app-types';
import { DEFAULT_CONTENT_TYPE_HEADERS, doRequest, HttpMethods, Headers, MediaTypes } from '../handlers/httpHandler';
import { env } from '../../env';

export const CAST_VOTE_URL = `${env.VOTING_APP_SERVER_URL}/api/vote/cast`;
export const VOTE_RECEIPT_URL = `${env.VOTING_APP_SERVER_URL}/api/vote/receipt`;
export const BLOCKCHAIN_TIP_URL = `${env.VOTING_LEDGER_FOLLOWER_APP_SERVER_URL}/api/blockchain/tip`;
export const VOTING_POWER_URL = `${env.VOTING_LEDGER_FOLLOWER_APP_SERVER_URL}/api/account`;
export const GOOGLE_FORM_URL = env.GOOGLE_FORM_URL;

export const ERRORS = {
  STAKE_AMOUNT_NOT_AVAILABLE: 'STAKE_AMOUNT_NOT_AVAILABLE',
};

export const castAVoteWithDigitalSignature = async (jsonRequest: SignedWeb3Request) =>
  await doRequest<void | Vote>(
    HttpMethods.POST,
    CAST_VOTE_URL,
    DEFAULT_CONTENT_TYPE_HEADERS,
    JSON.stringify(jsonRequest),
    undefined,
    true
  );

export const getChainTip = async () => {
  return await doRequest<ChainTip>(HttpMethods.GET, BLOCKCHAIN_TIP_URL, DEFAULT_CONTENT_TYPE_HEADERS);
};

export const getVoteReceipt = async (categoryId: string, token: string) =>
  await doRequest<void | VoteReceipt>(
    HttpMethods.GET,
    `${VOTE_RECEIPT_URL}/${env.EVENT_ID}/${categoryId}`,
    {
      ...DEFAULT_CONTENT_TYPE_HEADERS,
    },
    null,
    token
  );

export const getVotingPower = async (eventId: EventPresentation['id'], stakeAddress: string) => {
  return await doRequest<Account>(
    HttpMethods.GET,
    `${VOTING_POWER_URL}/${eventId}/${stakeAddress}`,
    DEFAULT_CONTENT_TYPE_HEADERS
  );
};

export const submitVoteContextForm = async (data: StringifiableRecord) =>
  await doRequest<void | void>(
    HttpMethods.POST,
    queryString.stringifyUrl({
      url: `${GOOGLE_FORM_URL}/formResponse`,
      query: {
        ...data,
      },
    }),
    {
      [Headers.CONTENT_TYPE]: MediaTypes.APPLICATION_JSON_UTF8_FORM_URLENCODED,
    }
  );
