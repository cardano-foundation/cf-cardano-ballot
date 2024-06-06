import {
  DEFAULT_CONTENT_TYPE_HEADERS,
  doRequest,
  HttpMethods,
} from "../handlers/httpHandler";
import { env } from "../constants/env";
import { EventCacheProps } from "../../store/reducers/eventCache/eventCache.types";

export const EVENT_BY_ID_REFERENCE_URL = `${env.VOTING_LEDGER_FOLLOWER_APP_SERVER_URL}/api/reference/event`;

export const getEventData = async (eventId: string | undefined) => {
  return await doRequest<EventCacheProps>(
    HttpMethods.GET,
    `${EVENT_BY_ID_REFERENCE_URL}/${eventId}`,
    {
      ...DEFAULT_CONTENT_TYPE_HEADERS,
    },
  );
};
