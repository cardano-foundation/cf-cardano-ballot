import { SignedWeb3Request } from "types/voting-app-types";
import {
  DEFAULT_CONTENT_TYPE_HEADERS,
  doRequest,
  HttpMethods,
} from "../handlers/httpHandler";
import { env } from "common/constants/env";
import { canonicalize } from "json-canonicalize";

export const LOGIN_URL = `${env.VOTING_APP_SERVER_URL}/api/auth/login`;

type LoginInput = {
  stakeAddress: string;
  slotNumber: string;
};

const buildCanonicalLoginJson = ({
  stakeAddress,
  slotNumber,
}: LoginInput): ReturnType<typeof canonicalize> => {
  return canonicalize({
    action: "LOGIN",
    actionText: "Login",
    slot: slotNumber,
    data: {
      address: stakeAddress,
      event: env.EVENT_ID,
      network: env.TARGET_NETWORK,
      role: "VOTER",
    },
  });
};

const submitLogin = async (jsonRequest: SignedWeb3Request) => {
  return await doRequest<{ accessToken: string; expiresAt: string }>(
    HttpMethods.GET,
    LOGIN_URL,
    DEFAULT_CONTENT_TYPE_HEADERS,
    JSON.stringify(jsonRequest),
    undefined,
    true,
  );
};

export { submitLogin, buildCanonicalLoginJson };
