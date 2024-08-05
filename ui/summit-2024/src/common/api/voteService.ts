import {
  DEFAULT_CONTENT_TYPE_HEADERS,
  doRequest,
  HttpMethods,
} from "../handlers/httpHandler";
import { canonicalize } from "json-canonicalize";
import { env } from "../constants/env";
import {
  Problem,
  SignedWeb3Request,
  UserVotes,
  Vote,
  VoteReceipt,
} from "../../types/voting-app-types";
import { ChainTip } from "../../types/voting-ledger-follower-types";
import { WalletIdentifierType } from "./utils";

export const CAST_VOTE_URL = `${env.VOTING_APP_SERVER_URL}/api/vote/cast`;
export const VOTE_RECEIPT_URL = `${env.VOTING_APP_SERVER_URL}/api/vote/receipt`;
export const BLOCKCHAIN_TIP_URL = `${env.VOTING_LEDGER_FOLLOWER_APP_SERVER_URL}/api/blockchain/tip`;
export const USER_VOTES_URL = `${env.VOTING_APP_SERVER_URL}/api/vote/votes`;

type voteInput = {
  voteId: string;
  proposalId: string;
  categoryId: string;
  walletId: string;
  walletType: WalletIdentifierType;
  slotNumber: string;
};

export const buildCanonicalVoteInputJson = ({
  voteId,
  categoryId,
  proposalId,
  walletId,
  slotNumber,
}: voteInput): ReturnType<typeof canonicalize> => {
  const startOfCurrentDay = new Date();
  startOfCurrentDay.setUTCMinutes(0, 0, 0);
  return canonicalize({
    action: "CAST_VOTE",
    actionText: "Cast Vote",
    slot: slotNumber,
    data: {
      id: voteId,
      walletId: walletId,
      event: env.EVENT_ID,
      category: categoryId,
      proposal: proposalId,
      network: env.TARGET_NETWORK,
      votedAt: slotNumber,
      votingEventType: "USER_BASED",
    },
  });
};

const submitVoteWithDigitalSignature = async (jsonRequest: SignedWeb3Request) =>
  await doRequest<Problem | Vote>(
    HttpMethods.POST,
    CAST_VOTE_URL,
    DEFAULT_CONTENT_TYPE_HEADERS,
    JSON.stringify(jsonRequest),
    undefined,
    true,
  );

const getSlotNumber = async () => {
  return await doRequest<ChainTip>(
    HttpMethods.GET,
    BLOCKCHAIN_TIP_URL,
    DEFAULT_CONTENT_TYPE_HEADERS,
  );
};

const getVoteReceipt = async (categoryId: string, token: string) =>
  await doRequest<VoteReceipt>(
    HttpMethods.GET,
    `${VOTE_RECEIPT_URL}/${env.EVENT_ID}/${categoryId}`,
    {
      ...DEFAULT_CONTENT_TYPE_HEADERS,
    },
    undefined,
    token,
  );

const getUserVotes = async (token: string) =>
  await doRequest<UserVotes[]>(
    HttpMethods.GET,
    `${USER_VOTES_URL}/${env.EVENT_ID}`,
    {
      ...DEFAULT_CONTENT_TYPE_HEADERS,
    },
    undefined,
    token,
  );

export {
  submitVoteWithDigitalSignature,
  getSlotNumber,
  getVoteReceipt,
  getUserVotes,
};
