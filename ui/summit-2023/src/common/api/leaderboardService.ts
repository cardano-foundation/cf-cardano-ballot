import { ByEvent } from 'types/voting-app-types';
import { DEFAULT_CONTENT_TYPE_HEADERS, doRequest, HttpMethods } from '../handlers/httpHandler';
import { env } from '../constants/env';

export const LEADERBOARD_URL = `${env.VOTING_APP_SERVER_URL}/api/leaderboard`;

export const getStats = async () =>
  await doRequest<ByEvent>(HttpMethods.GET, `${LEADERBOARD_URL}/${env.EVENT_ID}`, {
    ...DEFAULT_CONTENT_TYPE_HEADERS,
  });
