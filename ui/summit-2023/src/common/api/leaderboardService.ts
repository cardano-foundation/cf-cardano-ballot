import { ByEventStats, ByProposalsInCategoryStats } from 'types/voting-app-types';
import { DEFAULT_CONTENT_TYPE_HEADERS, doRequest, HttpMethods } from '../handlers/httpHandler';
import { env } from '../constants/env';

const LEADERBOARD_URL = `${env.VOTING_APP_SERVER_URL}/api/leaderboard`;

const getStats = async () =>
  await doRequest<ByEventStats>(HttpMethods.GET, `${LEADERBOARD_URL}/${env.EVENT_ID}`, {
    ...DEFAULT_CONTENT_TYPE_HEADERS,
  });

const getCategoryLevelStats = async (categoryId) => 
  await doRequest<ByProposalsInCategoryStats>(HttpMethods.GET, `${LEADERBOARD_URL}/${env.EVENT_ID}/${categoryId}`, {
    ...DEFAULT_CONTENT_TYPE_HEADERS,
  });

const getHydraTallyStats = async (categoryId) => 
  await doRequest<ByProposalsInCategoryStats>(HttpMethods.GET, `${LEADERBOARD_URL}/${env.EVENT_ID}/${categoryId}?source=l1`, {
    ...DEFAULT_CONTENT_TYPE_HEADERS,
  });

export { getStats, getCategoryLevelStats, getHydraTallyStats };
