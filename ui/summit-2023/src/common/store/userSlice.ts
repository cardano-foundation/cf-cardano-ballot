import { createSlice } from '@reduxjs/toolkit';
import type { PayloadAction } from '@reduxjs/toolkit';
import { VoteReceipt } from '../../types/voting-app-types';
import { EventPresentation } from '../../types/voting-ledger-follower-types';
import { UserState } from './types';

const initialState: UserState = {
  isConnectWalletModalVisible: false,
  isVoteSubmittedModalVisible: false,
  connectedWallet: '',
  isReceiptFetched: false,
  receipt: null,
  proposal: '',
  event: undefined,
};

export const userSlice = createSlice({
  name: 'user',
  initialState,
  reducers: {
    setIsConnectWalletModalVisible: (state, action: PayloadAction<{ isVisible: boolean }>) => {
      state.isConnectWalletModalVisible = action.payload.isVisible;
    },
    setIsVoteSubmittedModalVisible: (state, action: PayloadAction<{ isVisible: boolean }>) => {
      state.isVoteSubmittedModalVisible = action.payload.isVisible;
    },
    setConnectedWallet: (state, action: PayloadAction<{ wallet: string }>) => {
      state.connectedWallet = action.payload.wallet;
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

export const {
  setIsConnectWalletModalVisible,
  setIsVoteSubmittedModalVisible,
  setConnectedWallet,
  setVoteReceipt,
  setIsReceiptFetched,
  setSelectedProposal,
  setEventData,
} = userSlice.actions;
export default userSlice.reducer;
