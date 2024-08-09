import {
  DEFAULT_CONTENT_TYPE_HEADERS,
  doRequest,
  HttpMethods,
} from "../handlers/httpHandler";
import { canonicalize } from "json-canonicalize";
import { SignedWeb3Request } from "../../types/voting-app-types";
import { env } from "../constants/env";

export const LOGIN_URL = `${env.VOTING_APP_SERVER_URL}/api/auth/login`;

type LoginInput = {
  walletId: string;
  walletType: string;
  slotNumber: string;
};

const buildCanonicalLoginJson = ({
  walletId,
  walletType,
  slotNumber,
}: LoginInput): ReturnType<typeof canonicalize> => {
  return canonicalize({
    action: "LOGIN",
    actionText: "Login",
    slot: slotNumber,
    data: {
      event: env.EVENT_ID,
      network: env.TARGET_NETWORK,
      role: "VOTER",
      walletId: walletId,
      walletType: walletType,
    },
  });
};

const submitLogin = async (
  jsonRequest: SignedWeb3Request,
  walletType: string,
) => {
  return await doRequest<{ accessToken: string; expiresAt: string }>(
    HttpMethods.GET,
    LOGIN_URL,
    DEFAULT_CONTENT_TYPE_HEADERS,
    JSON.stringify({
      ...jsonRequest,
      walletType,
    }),
    undefined,
    true,
  );
};

export { submitLogin, buildCanonicalLoginJson };
