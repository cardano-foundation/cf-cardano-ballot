import { createSlice } from '@reduxjs/toolkit';
import type { PayloadAction } from '@reduxjs/toolkit';
import { VoteReceipt } from '../types/voting-app-types';
import { EventPresentation } from '../types/voting-ledger-follower-types';
import { UserState, VerificationStarts } from './types';

const initialState: UserState = {
  connectedWallet: '',
  walletIsVerified: false,
  isReceiptFetched: false,
  receipt: null,
  proposal: '',
  userVerification: {},
  event: {
    id: '',
    team: '',
    votingEventType: 'USER_BASED',
    startSlot: undefined,
    endSlot: undefined,
    startEpoch: undefined,
    eventStart: undefined,
    eventEnd: undefined,
    snapshotTime: undefined,
    endEpoch: undefined,
    snapshotEpoch: undefined,
    categories: [],
    active: true,
    notStarted: false,
    finished: false,
    allowVoteChanging: false,
    highLevelResultsWhileVoting: true,
    categoryResultsWhileVoting: false,
  },
};

export const userSlice = createSlice({
  name: 'user',
  initialState,
  reducers: {
    setConnectedWallet: (state, action: PayloadAction<{ wallet: string }>) => {
      state.connectedWallet = action.payload.wallet;
    },
    setWalletIsVerified: (state, action: PayloadAction<{ isVerified: boolean }>) => {
      state.walletIsVerified = action.payload.isVerified;
    },
    setVoteReceipt: (state, action: PayloadAction<{ receipt: VoteReceipt }>) => {
      state.receipt = action.payload.receipt;
    },
    setIsReceiptFetched: (state, action: PayloadAction<{ isFetched: boolean }>) => {
      state.isReceiptFetched = action.payload.isFetched;
    },
    setSelectedProposal: (state, action: PayloadAction<{ proposal: VoteReceipt['proposal'] }>) => {
      state.proposal = action.payload.proposal;
    },
    setEventData: (state, action: PayloadAction<{ event: EventPresentation }>) => {
      state.event = action.payload.event;
    },
    setUserStartsVerification: (
      state,
      action: PayloadAction<{ stakeAddress: string; verificationStarts: VerificationStarts }>
    ) => {
      state.userVerification[action.payload.stakeAddress] = action.payload.verificationStarts;
    },
  },
});

export const { setConnectedWallet, setVoteReceipt, setUserStartsVerification, setWalletIsVerified, setEventData } =
  userSlice.actions;
export default userSlice.reducer;
