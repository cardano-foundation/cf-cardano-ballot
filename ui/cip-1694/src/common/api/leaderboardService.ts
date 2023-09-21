import { ByProposalsInCategoryStats } from 'types/voting-app-types';
import { DEFAULT_CONTENT_TYPE_HEADERS, doRequest, HttpMethods } from '../handlers/httpHandler';
import { env } from '../../env';

export const LEADERBOARD_URL = `${env.VOTING_APP_SERVER_URL}/api/leaderboard`;

export const getStats = async (categoryId: string) =>
  await doRequest<ByProposalsInCategoryStats>(
    HttpMethods.GET,
    `${LEADERBOARD_URL}/${env.EVENT_ID}/${categoryId}`,
    {
      ...DEFAULT_CONTENT_TYPE_HEADERS,
    }
  );
