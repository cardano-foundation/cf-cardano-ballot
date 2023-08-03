import { DEFAULT_CONTENT_TYPE_HEADERS, doRequest, HttpMethods } from '../handlers/httpHandler';
import { env } from '../../env';
import { EventReference } from '../../types/backend-services-types';

export const getEvent = async (eventId: string) =>
  await doRequest<EventReference>(HttpMethods.GET, `${env.REFERENCE_DATA_URL}/${eventId}`, {
    ...DEFAULT_CONTENT_TYPE_HEADERS,
  });
