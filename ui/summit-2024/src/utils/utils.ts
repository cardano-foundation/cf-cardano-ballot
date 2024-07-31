import { SignedWeb3Request } from "../types/voting-app-types";
import { useCardano } from "@cardano-foundation/cardano-connect-with-wallet";
import { NetworkType } from "../components/ConnectWalletList/ConnectWalletList.types";

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
          console.log("getSignedMessagePromise");
          console.log("message");
          console.log(message);
          console.log("signature");
          console.log(signature);
          console.log("key");
          console.log(key);
          resolve({ coseSignature: signature, cosePublicKey: key || "" });
        },
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

const formatISODate = (isoDate: string): string | undefined => {
  if (!isoDate?.length) return undefined;

  const date = new Date(isoDate);

  const options: Intl.DateTimeFormatOptions = {
    day: "numeric",
    month: "long",
    year: "numeric",
    hour: "2-digit",
    minute: "2-digit",
    timeZone: "UTC",
    hourCycle: "h23",
    timeZoneName: "short",
  };

  const formatter = new Intl.DateTimeFormat("en-US", options);

  return formatter.format(date);
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
