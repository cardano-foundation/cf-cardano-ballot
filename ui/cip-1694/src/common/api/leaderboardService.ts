import { DEFAULT_CONTENT_TYPE_HEADERS, doRequest, HttpMethods } from '../handlers/httpHandler';
import { env } from '../../env';
import { ByCategory } from '../../types/backend-services-types';

export const getStats = async () =>
  await doRequest<ByCategory>(HttpMethods.GET, `${env.LEADERBOARD_URL}/${env.EVENT_ID}/${env.CATEGORY_ID}`, {
    ...DEFAULT_CONTENT_TYPE_HEADERS,
  });
