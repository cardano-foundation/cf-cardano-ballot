import { createSlice, PayloadAction } from "@reduxjs/toolkit";
import { RootState } from "../../index";
import {
  UserCacheProps,
  UserVotes,
  VerificationStarted,
} from "./userCache.types";
import { initialStateData } from "./initialState";

const initialState: UserCacheProps = initialStateData;

const userCacheSlice = createSlice({
  name: "userCache",
  initialState,
  reducers: {
    setUser: (state, action: PayloadAction<UserCacheProps>) => {
      return { ...state, ...action.payload };
    },
    setWalletIsVerified: (state, action: PayloadAction<boolean>) => {
      state.isVerified = action.payload;
    },
    setUserVotes: (state, action: PayloadAction<UserVotes[]>) => {
      state.userVotes = action.payload;
    },
    setVerificationStarted: (
      state,
      action: PayloadAction<VerificationStarted>,
    ) => {
      state.verificationStarted = action.payload;
    },
  },
});

const { setUser, setWalletIsVerified, setUserVotes, setVerificationStarted } =
  userCacheSlice.actions;

const getUser = (state: RootState) => state.userCache;
const getWalletIsVerified = (state: RootState) => state.userCache.isVerified;
const getUserVotes = (state: RootState) => state.userCache.userVotes;
const getVerificationStarted = (state: RootState) =>
  state.userCache.verificationStarted;

export {
  userCacheSlice,
  initialState,
  setUser,
  setWalletIsVerified,
  setUserVotes,
  setVerificationStarted,
  getUser,
  getWalletIsVerified,
  getUserVotes,
  getVerificationStarted,
};
