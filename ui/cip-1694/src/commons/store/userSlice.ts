import { createSlice } from '@reduxjs/toolkit';
import type { PayloadAction } from '@reduxjs/toolkit';
import { UserState } from './types';

const initialState: UserState = {
  isLoggedIn: false,
  isVerified: false,
  termsAndPrivacy: false,
  error: '',
};

export const userSlice = createSlice({
  name: 'user',
  initialState,
  reducers: {
    setSession: (state, action: PayloadAction<{ session: any }>) => {
      state = {
        ...action.payload.session,
        isLoggedIn: true,
      };
    },
    setUser: (state, action: PayloadAction<{ user: any }>) => {
      state.user = action.payload.user;
    },
    setUserIsVerified: (state, action: PayloadAction<{ isVerified: boolean }>) => {
      state.isVerified = action.payload.isVerified;
    },
    reduxError: (state, action: PayloadAction<{ error: string }>) => {
      state.error = action.payload.error;
    },
    clearSession: (state, action: PayloadAction<{ termsAndPrivacy: boolean }>) => {
      state = {
        ...initialState,
        termsAndPrivacy: action.payload.termsAndPrivacy,
      };
    },
  },
});

export const { setSession, setUser, setUserIsVerified, reduxError, clearSession } = userSlice.actions;
export default userSlice.reducer;
