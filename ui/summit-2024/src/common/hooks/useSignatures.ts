import {useCallback, useMemo, useState} from "react";
import { useCardano } from "@cardano-foundation/cardano-connect-with-wallet";
import { WalletIdentifierType } from "../api/utils";
import { Buffer } from 'buffer';
import {
  SignedKeriRequest,
  SignedWeb3Request,
} from "../../types/voting-app-types";
import {getSignedMessagePromise} from "../../utils/utils";

interface SignResponse {
  success: boolean;
  result?: SignedKeriRequest | SignedWeb3Request;
  error?: string;
}

export const useSignatures = () => {
  const { signMessage } = useCardano();
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const signMessagePromisified = useMemo(() => getSignedMessagePromise(signMessage), [signMessage]);

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
              signature: signedMessage,
              publicKey: keriIdentifier.id,
              payload: Buffer.from(message, 'utf8').toString('hex'),
              oobi: keriIdentifier.oobi,
            },
          };
        } else {
          const signedMessage = await signMessagePromisified(message);
          return {
            success: true,

            result: {
              signature: signedMessage.signature,
              publicKey: signedMessage.publicKey,
              payload: Buffer.from(message, 'utf8').toString('hex'),
            },
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
    [],
  );

  return {
    signWithWallet,
    isLoading,
    error,
  };
};
