import { configureStore } from '@reduxjs/toolkit';
import { persistReducer, persistStore } from 'redux-persist';
import thunk from 'redux-thunk';
import reduxReset from 'redux-reset';
import storage from 'redux-persist/lib/storage';
import userSessionReducer from './userSlice';

const userPersistConfig = {
  key: 'user',
  storage,
  blacklist: ['isLoggedIn'],
};

export const store = configureStore({
  reducer: persistReducer(userPersistConfig, userSessionReducer),
  devTools: process.env.NODE_ENV !== 'production',
  middleware: [thunk, reduxReset],
});

export const persistor = persistStore(store);

export type RootState = ReturnType<typeof store.getState>;
export type AppDispatch = typeof store.dispatch;
