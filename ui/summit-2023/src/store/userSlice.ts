import { createSlice } from '@reduxjs/toolkit';
import type { PayloadAction } from '@reduxjs/toolkit';
import { UserVotes, VoteReceipt } from '../types/voting-app-types';
import { EventPresentation } from '../types/voting-ledger-follower-types';
import { UserState, VerificationStarts } from './types';

const initialState: UserState = {
  connectedWallet: '',
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
  },
};

export const userSlice = createSlice({
  name: 'user',
  initialState,
  reducers: {
    setConnectedWallet: (state, action: PayloadAction<{ wallet: string }>) => {
      state.connectedWallet = action.payload.wallet;
    },
    setConnectedPeerWallet: (state, action: PayloadAction<{ peerWallet: boolean }>) => {
      state.connectedPeerWallet = action.payload.peerWallet;
    },
    setWalletIsVerified: (state, action: PayloadAction<{ isVerified: boolean }>) => {
      state.walletIsVerified = action.payload.isVerified;
    },
    setWalletIsLoggedIn: (state, action: PayloadAction<{ isLoggedIn: boolean }>) => {
      state.walletIsLoggedIn = action.payload.isLoggedIn;
    },
    setVoteReceipt: (state, action: PayloadAction<{ categoryId: string; receipt: VoteReceipt }>) => {
      state.receipts = {
        ...state.receipts,
        [action.payload.categoryId]: action.payload.receipt,
      };
    },
    setIsReceiptFetched: (state, action: PayloadAction<{ isFetched: boolean }>) => {
      state.isReceiptFetched = action.payload.isFetched;
    },
    setSelectedProposal: (state, action: PayloadAction<{ proposal: VoteReceipt['proposal'] }>) => {
      state.proposal = action.payload.proposal;
    },
    setUserVotes: (state, action: PayloadAction<{ userVotes: UserVotes[] }>) => {
      state.userVotes = action.payload.userVotes;
    },
    setEventData: (state, action: PayloadAction<{ event: EventPresentation }>) => {
      state.event = action.payload.event;
    },
    setWinners: (state, action: PayloadAction<{ winners: { categoryId: string; proposalId: string }[] }>) => {
      state.winners = action.payload.winners;
    },
    setUserStartsVerification: (
      state,
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
