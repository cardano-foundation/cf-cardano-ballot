import { SignedWeb3Request } from '../types/voting-app-types';
import { useCardano } from '@cardano-foundation/cardano-connect-with-wallet';
import { canonicalize } from 'json-canonicalize';
import { env } from 'common/constants/env';

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

type voteInput = {
  option: string;
  voteId: string;
  voter: string;
  slotNumber: string;
};

export const buildCanonicalVoteInputJson = ({
  option,
  voteId,
  voter,
  slotNumber,
}: voteInput): ReturnType<typeof canonicalize> => {
  const startOfCurrentDay = new Date();
  startOfCurrentDay.setUTCMinutes(0, 0, 0);
  return canonicalize({
    action: 'CAST_VOTE',
    actionText: 'Cast Vote',
    slot: slotNumber,
    data: {
      id: voteId,
      address: voter,
      event: env.EVENT_ID,
      category: env.CATEGORY_ID,
      proposal: option,
      network: env.TARGET_NETWORK,
      votedAt: slotNumber,
    },
  });
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

export { addressSlice, walletIcon, getSignedMessagePromise };
