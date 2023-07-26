import { configureStore } from '@reduxjs/toolkit';
import userSessionReducer from './userSlice';
import { State } from './types';

export const store = configureStore<State>({
  reducer: { user: userSessionReducer },
  devTools: process.env.NODE_ENV !== 'production',
});

export type RootState = ReturnType<typeof store.getState>;
export type AppDispatch = typeof store.dispatch;
