import { createSlice, PayloadAction } from "@reduxjs/toolkit";
import { RootState } from "../../index";
import { VoteCacheProps, VotesCacheProps } from "./votesCache.types";
import { initialStateData } from "./initialState";
import { VoteReceipt } from "../../../types/voting-app-types";

const initialState: VotesCacheProps = initialStateData;

const votesCacheSlice = createSlice({
  name: "receiptsCache",
  initialState,
  reducers: {
    setVotes: (state, action: PayloadAction<VoteCacheProps[]>) => {
      state.votes = action.payload;
    },
    setVoteReceipt: (
      state,
      action: PayloadAction<{ categoryId: string; receipt: VoteReceipt }>,
    ) => {
      state.receipts = {
        ...state.receipts,
        [action.payload.categoryId]: action.payload.receipt,
      };
    },
  },
});

const { setVotes, setVoteReceipt } = votesCacheSlice.actions;

const getVotes = (state: RootState) => state.votesCache.votes;
const getReceipts = (state: RootState) => state.votesCache.receipts;

export {
  votesCacheSlice,
  initialState,
  setVotes,
  setVoteReceipt,
  getVotes,
  getReceipts,
};
