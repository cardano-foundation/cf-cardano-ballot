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

const parseJwt = (token) => {
  if (token && token.length) {
    const base64Url = token.split('.')[1];
    const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
    const jsonPayload = decodeURIComponent(
      window
        .atob(base64)
        .split('')
        .map(function (c) {
          return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2);
        })
        .join('')
    );
    return JSON.parse(jsonPayload);
  } else {
    return undefined;
  }
};

export { addressSlice, walletIcon, getSignedMessagePromise, parseJwt };
