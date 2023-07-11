import { configureStore } from '@reduxjs/toolkit';
import { persistReducer, persistStore } from 'redux-persist';
import thunk from 'redux-thunk';
import reduxReset from 'redux-reset';
import storage from 'redux-persist/lib/storage';
import userSessionReducer from '../index';

const userPersistConfig = {
  key: 'user',
  storage,
  blacklist: ['isLoggedIn'],
};

const persistedReducer = persistReducer(userPersistConfig, userSessionReducer);

export const store = configureStore({
  reducer: persistedReducer,
  devTools: process.env.NODE_ENV !== 'production',
  middleware: [thunk],
  reduxReset,
});

export const persistor = persistStore(store);
