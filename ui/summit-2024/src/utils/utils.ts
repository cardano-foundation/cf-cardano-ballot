import { SignedWeb3Request } from "../types/voting-app-types";
import { useCardano } from "@cardano-foundation/cardano-connect-with-wallet";
import { NetworkType } from "@cardano-foundation/cardano-connect-with-wallet-core";

const addressSlice = (address: string, sliceLength = 10) => {
  if (address) {
    return `${address.slice(0, sliceLength)}...${address.slice(-sliceLength)}`;
  }
  return address;
};

const walletIcon = (walletName: string) => {
  if (!walletName?.length) return;
  return window.cardano && window.cardano[walletName]?.icon;
};

const getSignedMessagePromise = (
  signMessage: ReturnType<typeof useCardano>["signMessage"],
) => {
  return async (message: string): Promise<SignedWeb3Request> =>
    new Promise((resolve, reject) => {
      signMessage(
        message,
        (signature, key) =>
          resolve({ coseSignature: signature, cosePublicKey: key || "" }),
        (error: Error) => reject(error),
      );
    });
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

const shortenString = (inputStr: string, x: number): string => {
  if (inputStr.length <= x) {
    return inputStr;
  }
  return inputStr.slice(0, x) + "...";
};

export {
  addressSlice,
  walletIcon,
  getSignedMessagePromise,
  copyToClipboard,
  capitalizeFirstLetter,
  resolveCardanoNetwork,
  openNewTab,
  shortenString,
};
