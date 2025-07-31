import { createSlice, PayloadAction } from "@reduxjs/toolkit";
import { RootState } from "../../index";
import { UserCacheProps, VerificationStartedExtended } from "./userCache.types";
import { initialStateData } from "./initialState";
import { IWalletInfo } from "../../../components/ConnectWalletList/ConnectWalletList.types";

const initialState: UserCacheProps = initialStateData;

const userCacheSlice = createSlice({
  name: "userCache",
  initialState,
  reducers: {
    setUser: (state, action: PayloadAction<UserCacheProps>) => {
      return { ...state, ...action.payload };
    },
    resetUser: (_state) => {
      return initialState;
    },
    setConnectedWallet: (state, action: PayloadAction<IWalletInfo>) => {
      state.connectedWallet = action.payload;
    },
    setWalletIsVerified: (state, action: PayloadAction<boolean>) => {
      state.isVerified = action.payload;
    },
    setVerificationStarted: (
      state,
      action: PayloadAction<VerificationStartedExtended>,
    ) => {
      state.verificationStarted = action.payload;
    },
    setIsLogin: (state, action: PayloadAction<boolean>) => {
      state.isVerified = action.payload;
    },
  },
});

const {
  setUser,
  resetUser,
  setWalletIsVerified,
  setIsLogin,
  setConnectedWallet,
  setVerificationStarted,
} = userCacheSlice.actions;

const getUser = (state: RootState) => state.userCache;
const getWalletIsVerified = (state: RootState) => state.userCache.isVerified;
const getWalletIsLogin = (state: RootState) => state.userCache.isLogin;
const getConnectedWallet = (state: RootState) =>
  state.userCache.connectedWallet;
const getVerificationStarted = (state: RootState) =>
  state.userCache.verificationStarted;

export {
  userCacheSlice,
  initialState,
  setUser,
  resetUser,
  setWalletIsVerified,
  setVerificationStarted,
  setConnectedWallet,
  getUser,
  getWalletIsVerified,
  getWalletIsLogin,
  setIsLogin,
  getConnectedWallet,
  getVerificationStarted,
};
