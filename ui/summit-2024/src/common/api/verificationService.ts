import {
  DEFAULT_CONTENT_TYPE_HEADERS,
  doRequest,
  HttpMethods,
} from "../handlers/httpHandler";
import { env } from "../constants/env";
import {
  PhoneNumberCodeConfirmation
} from "../../store2/types";
import {
  MerkleProofItem,
  SignedWeb3Request,
} from "../../types/voting-app-types";
import {Problem} from "../../types/user-verification-app-types";
import {VoteVerificationResult} from "../../types/voting-verification-app-types";
import {resolveWalletIdentifierType, WalletIdentifierType} from "./utils";
import { v4 as uuidv4 } from 'uuid';
import {VerificationStartedExtended} from "../../store/reducers/userCache/userCache.types";

export const USER_VERIFICATION_URL = `${env.VOTING_USER_VERIFICATION_SERVER_URL}/api/user-verification/verified`;
export const VERIFICATION_URL = `${env.VOTING_VERIFICATION_APP_SERVER_URL}/api/verification/verify-vote`;
export const START_VERIFICATION_URL = `${env.VOTING_USER_VERIFICATION_SERVER_URL}/api/sms/user-verification/start-verification`;
export const CONFIRM_PHONE_NUMBER_CODE_URL = `${env.VOTING_USER_VERIFICATION_SERVER_URL}/api/sms/user-verification/check-verification`;
export const DISCORD_VERIFICATION_URL = `${env.VOTING_USER_VERIFICATION_SERVER_URL}/api/discord/user-verification/check-verification`;

export const verifyVote = async (payload: {
  rootHash: string;
  steps: MerkleProofItem[];
  voteCoseSignature: string;
  voteCosePublicKey: string;
}) =>
  await doRequest<Problem | VoteVerificationResult>(
    HttpMethods.POST,
    `${VERIFICATION_URL}`,
    DEFAULT_CONTENT_TYPE_HEADERS,
    JSON.stringify(payload),
  );

export const getIsVerified = async (walletIdentifier: string) => {

    if (resolveWalletIdentifierType(walletIdentifier) === WalletIdentifierType.Keri){
        // TODO: fake response
        return {
            verified: true
        }
    } else {
        return await doRequest<{ verified: boolean }>(
            HttpMethods.GET,
            `${USER_VERIFICATION_URL}/${env.EVENT_ID}/${walletIdentifier}`,
            {
                ...DEFAULT_CONTENT_TYPE_HEADERS,
            },
        );
    }
}

export const sendSmsCode = async (
    walletIdentifier: string,
  phoneNumber: string,
) => {
    if (resolveWalletIdentifierType(walletIdentifier) === WalletIdentifierType.Keri){
        const now = new Date();
        // TODO: fake response
        return {
            eventId: env.EVENT_ID,
            walletIdentifier: walletIdentifier,
            requestId: uuidv4(),
            createdAt: now.toISOString(),
            expiresAt: (new Date(now.getTime() + 5 * 60 * 1000)).toISOString()
        }
    } else {
        return await doRequest<VerificationStartedExtended>(
            HttpMethods.POST,
            `${START_VERIFICATION_URL}`,
            {
                ...DEFAULT_CONTENT_TYPE_HEADERS,
            },
            JSON.stringify({ eventId: env.EVENT_ID, stakeAddress: walletIdentifier, phoneNumber }),
        );
    }
};

export const confirmPhoneNumberCode = async (
    walletIdentifier: string,
  phoneNumber: string,
  requestId: string,
  verificationCode: string,
) => {
    if (resolveWalletIdentifierType(walletIdentifier) === WalletIdentifierType.Keri){
        return {
            verified: true
        }
    } else {
        return await doRequest<PhoneNumberCodeConfirmation>(
            HttpMethods.POST,
            `${CONFIRM_PHONE_NUMBER_CODE_URL}`,
            {
                ...DEFAULT_CONTENT_TYPE_HEADERS,
            },
            JSON.stringify({
                eventId: env.EVENT_ID,
                stakeAddress: walletIdentifier,
                phoneNumber,
                requestId,
                verificationCode,
            }),
        );
    }

}

export const verifyDiscord = async (
  stakeAddress: string,
  secret: string,
  signedMessaged: SignedWeb3Request,
) => {
  return await doRequest<{ verified: boolean }>(
    HttpMethods.POST,
    `${DISCORD_VERIFICATION_URL}`,
    {
      ...DEFAULT_CONTENT_TYPE_HEADERS,
    },
    JSON.stringify({ eventId: env.EVENT_ID, stakeAddress, secret, ...signedMessaged }),
  );
};
