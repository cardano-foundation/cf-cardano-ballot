import { DEFAULT_CONTENT_TYPE_HEADERS, doRequest, HttpMethods } from '../handlers/httpHandler';
import { env } from '../../env';
import { EventReference } from '../../types/backend-services-types';

export const EVENT_BY_ID_REFERENCE_URL = `${env.APP_SERVER_URL}/api/reference/event`;

export const getEvent = async (eventId: string) =>
  await doRequest<EventReference>(HttpMethods.GET, `${EVENT_BY_ID_REFERENCE_URL}/${eventId}`, {
    ...DEFAULT_CONTENT_TYPE_HEADERS,
  });
