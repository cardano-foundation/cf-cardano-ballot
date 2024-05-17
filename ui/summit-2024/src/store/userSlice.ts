import { createSlice } from '@reduxjs/toolkit';
import type { PayloadAction } from '@reduxjs/toolkit';
import { UserVotes, VoteReceipt } from '../types/voting-app-types';
import { EventPresentation } from '../types/voting-ledger-follower-types';
import { StateProps, VerificationStarts } from './types';

const initialState: StateProps = {
  connectedPeerWallet: false,
  walletIsVerified: false,
  walletIsLoggedIn: false,
  isReceiptFetched: false,
  receipts: {},
  winners: [],
  userVotes: [],
  proposal: '',
  userVerification: {},
  event: {
    id: '',
    organisers: '',
    votingEventType: 'USER_BASED',
    startSlot: undefined,
    endSlot: undefined,
    proposalsRevealSlot: undefined,
    startEpoch: undefined,
    eventStartDate: undefined,
    eventEndDate: undefined,
    proposalsRevealDate: undefined,
    snapshotTime: undefined,
    endEpoch: undefined,
    snapshotEpoch: undefined,
    proposalsRevealEpoch: undefined,
    categories: [],
    active: false,
    notStarted: false,
    finished: false,
    proposalsReveal: false,
    allowVoteChanging: false,
    highLevelEventResultsWhileVoting: true,
    categoryResultsWhileVoting: true,
    highLevelCategoryResultsWhileVoting: false,
    commitmentsWindowOpen: false,
    started: false,
    tallies: []
  },
};

export const userSlice = createSlice({
  name: 'user',
  initialState,
  reducers: {
    setConnectedPeerWallet: (state: StateProps, action: PayloadAction<{ peerWallet: boolean }>) => {
      state.connectedPeerWallet = action.payload.peerWallet;
    },
    setWalletIsVerified: (state: StateProps, action: PayloadAction<{ isVerified: boolean }>) => {
      state.walletIsVerified = action.payload.isVerified;
    },
    setWalletIsLoggedIn: (state: StateProps, action: PayloadAction<{ isLoggedIn: boolean }>) => {
      state.walletIsLoggedIn = action.payload.isLoggedIn;
    },
    setVoteReceipt: (state: StateProps, action: PayloadAction<{ categoryId: string; receipt: VoteReceipt }>) => {
      if (!action.payload.categoryId.length) {
        state.receipts = {};
        return;
      }
      state.receipts = {
        ...state.receipts,
        [action.payload.categoryId]: action.payload.receipt,
      };
    },
    setIsReceiptFetched: (state: StateProps, action: PayloadAction<{ isFetched: boolean }>) => {
      state.isReceiptFetched = action.payload.isFetched;
    },
    setSelectedProposal: (state: StateProps, action: PayloadAction<{ proposal: VoteReceipt['proposal'] }>) => {
      state.proposal = action.payload.proposal;
    },
    setUserVotes: (state: StateProps, action: PayloadAction<{ userVotes: UserVotes[] }>) => {
      state.userVotes = action.payload.userVotes;
    },
    setEventData: (state: StateProps, action: PayloadAction<{ event: EventPresentation }>) => {
      state.event = action.payload.event;
    },
    setWinners: (state: StateProps, action: PayloadAction<{ winners: { categoryId: string; proposalId: string }[] }>) => {
      let filteredWinners = state.winners.filter(
        (oldWinner) => !action.payload.winners.some((winner) => winner.categoryId === oldWinner.categoryId)
      );
      filteredWinners = [...filteredWinners, ...action.payload.winners];
      state.winners = filteredWinners;
    },
    setUserStartsVerification: (
      state: StateProps,
      action: PayloadAction<{ stakeAddress: string; verificationStarts: VerificationStarts }>
    ) => {
      state.userVerification[action.payload.stakeAddress] = action.payload.verificationStarts;
    },
  },
});

export const {
  setVoteReceipt,
  setConnectedPeerWallet,
  setWalletIsLoggedIn,
  setUserStartsVerification,
  setWalletIsVerified,
  setUserVotes,
  setEventData,
  setWinners,
} = userSlice.actions;
export default userSlice.reducer;
