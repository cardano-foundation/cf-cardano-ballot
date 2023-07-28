import { DEFAULT_CONTENT_TYPE_HEADERS, doRequest, HttpMethods } from '../handlers/httpHandler';
import { LEADERBOARD_URL, EVENT_ID, CATEGORY_ID } from '../constants/appConstants';
import { ByCategory } from '../../types/backend-services-types';

export const getStats = async () =>
  await doRequest<ByCategory>(HttpMethods.GET, `${LEADERBOARD_URL}/${EVENT_ID}/${CATEGORY_ID}`, {
    ...DEFAULT_CONTENT_TYPE_HEADERS,
  });
