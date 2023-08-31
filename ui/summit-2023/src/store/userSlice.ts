import { createSlice } from '@reduxjs/toolkit';
import type { PayloadAction } from '@reduxjs/toolkit';
import { VoteReceipt } from '../types/voting-app-types';
import { EventPresentation } from '../types/voting-ledger-follower-types';
import { UserState } from './types';

const initialState: UserState = {
  connectedWallet: '',
  walletIsVerified: false,
  isReceiptFetched: false,
  receipt: null,
  proposal: '',
  event: undefined,
};

export const userSlice = createSlice({
  name: 'user',
  initialState,
  reducers: {
    setConnectedWallet: (state, action: PayloadAction<{ wallet: string }>) => {
      state.connectedWallet = action.payload.wallet;
    },
    setWalletIsVerified: (state, action: PayloadAction<{ isVerified: string }>) => {
      state.connectedWallet = action.payload.isVerified;
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
  },
});

export const { setConnectedWallet, setVoteReceipt, setIsReceiptFetched, setSelectedProposal, setEventData } =
  userSlice.actions;
export default userSlice.reducer;
