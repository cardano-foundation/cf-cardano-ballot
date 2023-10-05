import { canonicalize } from 'json-canonicalize';
import { SignedWeb3Request } from 'types/voting-app-types';
import { useCardano } from '@cardano-foundation/cardano-connect-with-wallet';
import { env } from '../../env';

type voteInput = {
  option: string;
  voteId: string;
  voter: string;
  slotNumber: string;
  votingPower: string;
  category: string;
};

export const buildCanonicalVoteInputJson = ({
  option,
  voteId,
  voter,
  slotNumber,
  votingPower,
  category,
}: voteInput): ReturnType<typeof canonicalize> => {
  const startOfCurrentDay = new Date();
  startOfCurrentDay.setUTCMinutes(0, 0, 0);
  return canonicalize({
    // TODO: should this one be hardcoded?
    uri: 'https://evoting.cardano.org/voltaire',
    action: 'CAST_VOTE',
    actionText: 'Cast Vote',
    slot: slotNumber,
    data: {
      id: voteId,
      address: voter,
      event: env.EVENT_ID,
      category,
      proposal: option,
      network: env.TARGET_NETWORK,
      votedAt: slotNumber,
      votingPower,
    },
  });
};

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
