import { EventPresentation } from '../../types/voting-ledger-follower-types';
import { DEFAULT_CONTENT_TYPE_HEADERS, doRequest, HttpMethods } from '../handlers/httpHandler';
import { env } from '../constants/env';

export const EVENT_BY_ID_REFERENCE_URL = `${env.VOTING_LEDGER_FOLLOWER_APP_SERVER_URL}/api/reference/event`;

export const getEvent = async (eventId: string | undefined) =>
  await doRequest<EventPresentation>(HttpMethods.GET, `${EVENT_BY_ID_REFERENCE_URL}/${eventId}`, {
    ...DEFAULT_CONTENT_TYPE_HEADERS,
  });
