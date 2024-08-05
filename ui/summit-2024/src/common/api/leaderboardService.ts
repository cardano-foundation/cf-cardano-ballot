import {
  DEFAULT_CONTENT_TYPE_HEADERS,
  doRequest,
  HttpMethods,
} from "../handlers/httpHandler";
import { env } from "../constants/env";
import {
  ByEventStats,
  ByProposalsInCategoryStats,
} from "../../types/voting-app-types";
import { TallyResults } from "../../types/voting-ledger-follower-types";

const LEADERBOARD_URL = `${env.VOTING_APP_SERVER_URL}/api/leaderboard`;
const HYDRATALLY_URL = `${env.VOTING_LEDGER_FOLLOWER_APP_SERVER_URL}/api/tally/voting-results`;

const getStats = async () =>
  await doRequest<ByEventStats>(
    HttpMethods.GET,
    `${LEADERBOARD_URL}/${env.EVENT_ID}`,
    {
      ...DEFAULT_CONTENT_TYPE_HEADERS,
    },
  );

const getVotingResults = async () =>
  await doRequest<ByProposalsInCategoryStats>(
    HttpMethods.GET,
    `${LEADERBOARD_URL}/${env.EVENT_ID}/results?source=db`,
    {
      ...DEFAULT_CONTENT_TYPE_HEADERS,
    },
  );

const getHydraTallyStats = async () =>
  await doRequest<TallyResults>(
    HttpMethods.GET,
    `${HYDRATALLY_URL}/${env.EVENT_ID}/Hydra_Tally_Experiment`,
    {
      ...DEFAULT_CONTENT_TYPE_HEADERS,
    },
  );

export { getStats, getVotingResults, getHydraTallyStats };
