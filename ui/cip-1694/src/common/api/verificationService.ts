import { DEFAULT_CONTENT_TYPE_HEADERS, doRequest, HttpMethods } from '../handlers/httpHandler';
import { env } from '../../env';
import { VoteVerificationRequest, Problem } from '../../types/verification-service-types';

export const VERIFICATION_URL = `${env.APP_SERVER_URL}/api/verification/verify-vote`;

export const verifyVote = async (payload: VoteVerificationRequest) =>
  await doRequest<Problem | boolean>(
    HttpMethods.POST,
    `${VERIFICATION_URL}`,
    DEFAULT_CONTENT_TYPE_HEADERS,
    JSON.stringify(payload)
  );
