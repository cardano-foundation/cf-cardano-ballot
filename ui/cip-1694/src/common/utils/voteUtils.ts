import { canonicalize } from 'json-canonicalize';
import { SignedWeb3Request } from 'types/voting-app-types';
import { useCardano } from '@cardano-foundation/cardano-connect-with-wallet';
import { env } from '../../env';

type voteInput = {
  option: string;
  voteId: string;
  voter: string;
  slotNumber: string;
  votePower: string;
};

export const buildCanonicalVoteInputJson = ({
  option,
  voteId,
  voter,
  slotNumber,
  votePower,
}: voteInput): ReturnType<typeof canonicalize> => {
  const startOfCurrentDay = new Date();
  startOfCurrentDay.setUTCMinutes(0, 0, 0);
  return canonicalize({
    // TOOD: move to const/env file/ config file, also the link seems to be broken
    uri: 'https://evoting.cardano.org/voltaire',
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
      votingPower: votePower,
    },
  });
};

type votereceiptInput = {
  voter: string;
  slotNumber: string;
};

export const buildCanonicalVoteReceiptInputJson = ({
  voter,
  slotNumber,
}: votereceiptInput): ReturnType<typeof canonicalize> =>
  canonicalize({
    uri: 'https://evoting.cardano.org/voltaire',
    action: 'VIEW_VOTE_RECEIPT',
    actionText: 'View Vote Receipt',
    slot: slotNumber,
    data: {
      address: voter,
      event: env.EVENT_ID,
      category: env.CATEGORY_ID,
      network: env.TARGET_NETWORK,
    },
  });

export const getSignedMessagePromise = (signMessage: ReturnType<typeof useCardano>['signMessage']) => {
  return async (message: string): Promise<SignedWeb3Request> =>
    new Promise((resolve, reject) => {
      signMessage(
        message,
        (signature, key) => resolve({ coseSignature: signature, cosePublicKey: key || '' }),
        (error: Error) => reject(error)
      );
    });
};
