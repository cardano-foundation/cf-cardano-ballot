import { UserVotes, VoteReceipt } from '../types/voting-app-types';
import { EventPresentation, ProposalPresentation } from '../types/voting-ledger-follower-types';

interface UserState {
  connectedWallet: string;
  connectedPeerWallet: boolean;
  walletIsVerified: boolean;
  walletIsLoggedIn: boolean;
  isReceiptFetched: boolean;
  receipts: { [categoryId: string]: VoteReceipt } | {};
  winners: { categoryId: string; proposalId: string }[];
  userVotes: UserVotes[];
  proposal: VoteReceipt['proposal'];
  event?: EventPresentation;
  userVerification?: {
    [stakeAddress: string]: VerificationStarts;
  };
}

interface ProposalPresentationExtended extends ProposalPresentation {
  desc?: string;
  presentationName?: string;
}

interface VerificationStarts {
  eventId: string;
  stakeAddress: string;
  requestId: string;
  createdAt: string;
  expiresAt: string;
}

interface PhoneNumberCodeConfirmation {
  verified: boolean;
}

interface State {
  user: UserState;
}

export { State, PhoneNumberCodeConfirmation, VerificationStarts, ProposalPresentationExtended, UserState };
