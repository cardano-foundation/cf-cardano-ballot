import { ByEventStats } from 'types/voting-app-types';
import { DEFAULT_CONTENT_TYPE_HEADERS, doRequest, HttpMethods } from '../handlers/httpHandler';
import { env } from '../constants/env';

const LEADERBOARD_URL = `${env.VOTING_APP_SERVER_URL}/api/leaderboard`;

const getStats = async () =>
  await doRequest<ByEventStats>(HttpMethods.GET, `${LEADERBOARD_URL}/${env.EVENT_ID}`, {
    ...DEFAULT_CONTENT_TYPE_HEADERS,
  });

const getCategoryLevelStats = async (categoryId) =>
  await doRequest<ByEventStats>(HttpMethods.GET, `${LEADERBOARD_URL}/${env.EVENT_ID}/${categoryId}`, {
    ...DEFAULT_CONTENT_TYPE_HEADERS,
  });

const getWinners = async () =>
  await doRequest<{ categoryId; proposalId }[]>(HttpMethods.GET, `${LEADERBOARD_URL}/${env.EVENT_ID}/winners`, {
    ...DEFAULT_CONTENT_TYPE_HEADERS,
  });

export { getStats, getCategoryLevelStats, getWinners };
