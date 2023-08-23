import { EventPresentation } from 'types/voting-ledger-follower-types';
import { DEFAULT_CONTENT_TYPE_HEADERS, doRequest, HttpMethods } from '../handlers/httpHandler';
import { env } from '../../env';

export const EVENT_BY_ID_REFERENCE_URL = `${env.APP_SERVER_URL}/api/reference/event`;

export const getEvent = async (eventId: string) =>
  await doRequest<EventPresentation>(HttpMethods.GET, `${EVENT_BY_ID_REFERENCE_URL}/${eventId}`, {
    ...DEFAULT_CONTENT_TYPE_HEADERS,
  });
