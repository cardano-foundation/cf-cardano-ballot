import { configureStore, PreloadedState, combineReducers } from '@reduxjs/toolkit';
import userSessionReducer from './userSlice';

const rootReducer = combineReducers({
  user: userSessionReducer,
});

export const setupStore = (preloadedState?: PreloadedState<RootState>) => {
  return configureStore({
    reducer: rootReducer,
    preloadedState,
    devTools: process.env.NODE_ENV !== 'production',
  });
};

export type RootState = ReturnType<typeof rootReducer>;
export type AppStore = ReturnType<typeof setupStore>;
export type AppDispatch = AppStore['dispatch'];
