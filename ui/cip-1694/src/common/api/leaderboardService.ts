import { ByCategory } from 'types/voting-app-types';
import { DEFAULT_CONTENT_TYPE_HEADERS, doRequest, HttpMethods } from '../handlers/httpHandler';
import { env } from '../../env';

export const LEADERBOARD_URL = `${env.APP_SERVER_URL}/api/leaderboard`;

export const getStats = async () =>
  await doRequest<ByCategory>(HttpMethods.GET, `${LEADERBOARD_URL}/${env.EVENT_ID}/${env.CATEGORY_ID}`, {
    ...DEFAULT_CONTENT_TYPE_HEADERS,
  });
