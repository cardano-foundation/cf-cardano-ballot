import { SignedWeb3Request } from "../types/voting-app-types";
import { useCardano } from "@cardano-foundation/cardano-connect-with-wallet";
import { NetworkType } from "../components/ConnectWalletList/ConnectWalletList.types";
import { resolveWalletType, WalletIdentifierType } from "../common/api/utils";
import { Buffer } from "buffer";

const addressSlice = (
  address: string,
  sliceLength = 10,
  dotsPosition: "middle" | "end" = "middle",
) => {
  if (!address) return address;
  if (address.length <= 2 * sliceLength) return address;

  switch (dotsPosition) {
    case "end":
      return `${address.slice(0, sliceLength)}...`;
    case "middle":
    default:
      return `${address.slice(0, sliceLength)}...${address.slice(
        -sliceLength,
      )}`;
  }
};

const walletIcon = (walletName: string | null) => {
  if (!walletName?.length) return;
  // @ts-ignore
  return window.cardano && window.cardano[walletName]?.icon;
};

const getSignedMessagePromise = (
  signMessage: ReturnType<typeof useCardano>["signMessage"],
) => {
  return async (message: string): Promise<SignedWeb3Request> =>
    new Promise((resolve, reject) => {
      signMessage(
        message,
        (signature, key) => {
          resolve({ signature: signature, publicKey: key || "" });
        },
        (error: Error) => reject(error),
      );
    });
};

export const signMessageWithWallet = async (
  connectedWallet,
  canonicalLoginInput,
  signMessagePromisified,
) => {
  try {
    if (
      resolveWalletType(connectedWallet.address) ===
        WalletIdentifierType.KERI &&
      window.cardano &&
      window.cardano["idw_p2p"]
    ) {
      const api = window.cardano["idw_p2p"];
      const enabledApi = await api.enable();
      const keriIdentifier = await enabledApi.experimental.getKeriIdentifier();
      const signedMessage = await enabledApi.experimental.signKeri(
        connectedWallet.address,
        canonicalLoginInput,
      );
      if (signedMessage.error) {
        return {
          success: false,
          error:
            signedMessage.error.code === 2
              ? "User declined to sign"
              : signedMessage.error.info,
        };
      }

      return {
        success: true,
        result: {
          signature: signedMessage,
          publicKey: keriIdentifier.id,
          payload: Buffer.from(canonicalLoginInput, "utf8").toString("hex"),
          oobi: keriIdentifier.oobi,
        },
      };
    } else {
      const signedMessage = await signMessagePromisified(canonicalLoginInput);

      if (signedMessage.error) {
        return {
          success: false,
          error:
            signedMessage.error.code === 2
              ? "User declined to sign"
              : signedMessage.error.info,
        };
      }

      return {
        success: true,
        result: {
          signature: signedMessage.signature,
          publicKey: signedMessage.publicKey,
          payload: Buffer.from(canonicalLoginInput, "utf8").toString("hex"),
        },
      };
    }
  } catch (error) {
    if (error instanceof Error) {
      return {
        success: false,
        error: error.message,
      };
    } else {
      return {
        success: false,
        error: "An unknown error occurred while signing",
      };
    }
  }
};

const copyToClipboard = async (textToCopy: string) => {
  await navigator.clipboard.writeText(textToCopy);
};

const openNewTab = async (url: string) => {
  window.open(url, "_blank");
};

const capitalizeFirstLetter = (input: string): string => {
  if (!input || typeof input !== "string") {
    return "";
  }
  return input.trim().charAt(0).toUpperCase() + input.slice(1);
};

const resolveCardanoNetwork = (network: string): NetworkType => {
  if (["MAINNET", "MAIN"].includes(network.toUpperCase())) {
    return NetworkType.MAINNET;
  } else {
    return NetworkType.TESTNET;
  }
};

const formatISODate = (isoDate: string): string | undefined => {
  if (!isoDate?.length) return undefined;

  const date = new Date(isoDate);

  const options: Intl.DateTimeFormatOptions = {
    day: "numeric",
    month: "long",
    year: "numeric",
    hour: "2-digit",
    minute: "2-digit",
    hourCycle: "h23",
  };

  const formatter = new Intl.DateTimeFormat("en-US", options);

  return formatter.format(date) + " UTC";
};

export {
  addressSlice,
  walletIcon,
  getSignedMessagePromise,
  copyToClipboard,
  capitalizeFirstLetter,
  resolveCardanoNetwork,
  openNewTab,
  formatISODate,
};
