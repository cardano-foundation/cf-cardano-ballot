import { VoteVerificationRequest, Problem, VoteVerificationResult } from 'types/voting-verification-app-types';
import { DEFAULT_CONTENT_TYPE_HEADERS, doRequest, HttpMethods } from '../handlers/httpHandler';
import { env } from '../constants/env';
import { PhoneNumberCodeConfirmation, VerificationStarts } from '../../store/types';

export const USER_VERIFICATION_URL = `${env.VOTING_USER_VERIFICATION_SERVER_URL}/api/user-verification/verified`;
export const VERIFICATION_URL = `${env.VOTING_VERIFICATION_APP_SERVER_URL}/api/verification/verify-vote`;
export const START_VERIFICATION_URL = `${env.VOTING_USER_VERIFICATION_SERVER_URL}/api/sms/user-verification/start-verification`;
export const CONFIRM_PHONE_NUMBER_CODE = `${env.VOTING_USER_VERIFICATION_SERVER_URL}/api/sms/user-verification/check-verification`;

export const verifyVote = async (payload: VoteVerificationRequest) =>
  await doRequest<Problem | VoteVerificationResult>(
    HttpMethods.POST,
    `${VERIFICATION_URL}`,
    DEFAULT_CONTENT_TYPE_HEADERS,
    JSON.stringify(payload)
  );

export const getIsVerified = async (eventId: string, stakeAddress: string) =>
  await doRequest<{ verified: boolean }>(HttpMethods.GET, `${USER_VERIFICATION_URL}/${eventId}/${stakeAddress}`, {
    ...DEFAULT_CONTENT_TYPE_HEADERS,
  });

export const startVerification = async (eventId: string, stakeAddress: string, phoneNumber: string) =>
  await doRequest<VerificationStarts>(
    HttpMethods.POST,
    `${START_VERIFICATION_URL}`,
    {
      ...DEFAULT_CONTENT_TYPE_HEADERS,
    },
    JSON.stringify({ eventId, stakeAddress, phoneNumber })
  );

export const confirmPhoneNumberCode = async (
  eventId: string,
  stakeAddress: string,
  phoneNumber: string,
  requestId: string,
  verificationCode: string
) =>
  await doRequest<PhoneNumberCodeConfirmation>(
    HttpMethods.POST,
    `${CONFIRM_PHONE_NUMBER_CODE}`,
    {
      ...DEFAULT_CONTENT_TYPE_HEADERS,
    },
    JSON.stringify({ eventId, stakeAddress, phoneNumber, requestId, verificationCode })
  );
