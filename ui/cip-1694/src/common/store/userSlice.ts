import { createSlice } from '@reduxjs/toolkit';
import type { PayloadAction } from '@reduxjs/toolkit';
import { ChainTip, EventPresentation } from 'types/voting-ledger-follower-types';
import { UserState } from './types';

const initialState: UserState = {
  isConnectWalletModalVisible: false,
  isVoteSubmittedModalVisible: false,
  connectedWallet: '',
  event: null,
  tip: null,
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
    setEventData: (state, action: PayloadAction<{ event: EventPresentation }>) => {
      state.event = action.payload.event;
    },
    setChainTipData: (state, action: PayloadAction<{ tip: ChainTip }>) => {
      state.tip = action.payload.tip;
    },
  },
});

export const {
  setIsConnectWalletModalVisible,
  setIsVoteSubmittedModalVisible,
  setConnectedWallet,
  setEventData,
  setChainTipData,
} = userSlice.actions;
export default userSlice.reducer;
