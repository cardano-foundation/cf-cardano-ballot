import { useCallback, useState } from "react";
import { useCardano } from "@cardano-foundation/cardano-connect-with-wallet";
import { WalletIdentifierType } from "../api/utils";
import {
  SignedKeriRequest,
  SignedWeb3Request,
} from "../../types/voting-app-types";

interface SignResponse {
  success: boolean;
  result?: SignedKeriRequest | SignedWeb3Request;
  error?: string;
}

export const useSignatures = () => {
  const { signMessage } = useCardano();
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const signWithWallet = useCallback(
    async (
      message: string,
      walletIdentifier: string,
      messageType?: WalletIdentifierType,
    ): Promise<SignResponse> => {
      setIsLoading(true);
      setError(null);

      try {
        if (
          messageType &&
          messageType === WalletIdentifierType.KERI &&
          window.cardano &&
          window.cardano["idw_p2p"]
        ) {
          const api = window.cardano["idw_p2p"];
          const enabledApi = await api.enable();
          const keriIdentifier =
            await enabledApi.experimental.getKeriIdentifier();

          const signedMessage: string = await enabledApi.experimental.signKeri(
            walletIdentifier,
            message,
          );
          return {
            success: true,
            result: {
              keriPayload: message,
              keriSignedMessage: signedMessage,
              oobi: keriIdentifier.oobi,
            },
          };
        } else {
          const signedMessage = await signMessage(message);
          return {
            success: true,
            // @ts-ignore
            result: signedMessage,
          };
        }
      } catch (e) {
        // @ts-ignore
        setError(`Error signing message: ${e.message}`);
        setIsLoading(false);
        // @ts-ignore
        return { success: false, error: e.message };
      } finally {
        setIsLoading(false);
      }
    },
    [signMessage],
  );

  return {
    signWithWallet,
    isLoading,
    error,
  };
};
