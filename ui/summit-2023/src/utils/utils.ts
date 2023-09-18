import { SignedWeb3Request } from '../types/voting-app-types';
import { useCardano } from '@cardano-foundation/cardano-connect-with-wallet';

const addressSlice = (address: string, sliceLength = 10) => {
  if (address) {
    return `${address.slice(0, sliceLength)}...${address.slice(-sliceLength)}`;
  }
  return address;
};

const walletIcon = (walletName: string) => {
  // eslint-disable-next-line @typescript-eslint/ban-ts-comment
  // @ts-ignore
  return window.cardano && window.cardano[walletName].icon;
};

const getSignedMessagePromise = (signMessage: ReturnType<typeof useCardano>['signMessage']) => {
  return async (message: string): Promise<SignedWeb3Request> =>
    new Promise((resolve, reject) => {
      signMessage(
        message,
        (signature, key) => resolve({ coseSignature: signature, cosePublicKey: key || '' }),
        (error: Error) => reject(error)
      );
    });
};

const copyToClipboard = async (textToCopy: string) => {
  await navigator.clipboard.writeText(textToCopy);
};

const capitalizeFirstLetter =  (input: string):string => {
    if (!input || typeof input !== 'string') {
        return '';
    }
    return input.charAt(0).toUpperCase() + input.slice(1);
}

export { addressSlice, walletIcon, getSignedMessagePromise, copyToClipboard , capitalizeFirstLetter};
