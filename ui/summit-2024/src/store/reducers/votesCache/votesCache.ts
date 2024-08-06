import { createSlice, PayloadAction } from "@reduxjs/toolkit";
import { RootState } from "../../index";
import { VotesCacheProps } from "./votesCache.types";
import { initialStateData } from "./initialState";
import { UserVotes, VoteReceipt } from "../../../types/voting-app-types";

const initialState: VotesCacheProps = initialStateData;

const votesCacheSlice = createSlice({
  name: "receiptsCache",
  initialState,
  reducers: {
    setVote: (
      state,
      action: PayloadAction<{ categoryId: string; vote: UserVotes }>,
    ) => {
      state.votes = {
        ...state.votes,
        [action.payload.categoryId]: action.payload.vote.proposalId,
      };
    },
    setVotes: (
      state,
      action: PayloadAction<{ votes: { [categoryId: string]: string } }>,
    ) => {
      state.votes = action.payload.votes;
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

const { setVote, setVotes, setVoteReceipt } = votesCacheSlice.actions;

const getVotes = (state: RootState) => state.votesCache.votes;
const getReceipts = (state: RootState) => state.votesCache.receipts;

export {
  votesCacheSlice,
  initialState,
  setVote,
  setVotes,
  setVoteReceipt,
  getVotes,
  getReceipts,
};
