import { DEFAULT_CONTENT_TYPE_HEADERS, doRequest, HttpMethods } from '../handlers/httpHandler';
import { env } from '../../env';
import { VoteVerificationRequest, Problem } from '../../types/verification-service-types';

export const verifyVote = async (payload: VoteVerificationRequest) =>
  await doRequest<Problem | boolean>(
    HttpMethods.POST,
    `${env.VERIFICATION_URL}`,
    DEFAULT_CONTENT_TYPE_HEADERS,
    JSON.stringify(payload)
  );
