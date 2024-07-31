import { createSlice, PayloadAction } from "@reduxjs/toolkit";
import { RootState } from "../../index";
import { ReceiptsCacheProps } from "./receiptsCache.types";
import { initialStateData } from "./initialState";
import { VoteReceipt } from "../../../types/voting-app-types";

const initialState: ReceiptsCacheProps = initialStateData;

const receiptsCacheSlice = createSlice({
  name: "receiptsCache",
  initialState,
  reducers: {
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

const { setVoteReceipt } = receiptsCacheSlice.actions;

const getReceipts = (state: RootState) => state.receiptsCache;

export { receiptsCacheSlice, initialState, setVoteReceipt, getReceipts };
