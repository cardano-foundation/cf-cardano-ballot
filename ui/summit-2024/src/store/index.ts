import { configureStore } from "@reduxjs/toolkit";
import { eventCacheSlice } from "./reducers/eventCache";
import { userCacheSlice } from "./reducers/userCache";
import { receiptsCacheSlice } from "./reducers/receiptsCache/receiptsCache";

const store = configureStore({
  reducer: {
    eventCache: eventCacheSlice.reducer,
    userCache: userCacheSlice.reducer,
    receiptsCache: receiptsCacheSlice.reducer,
  },
});

type RootState = ReturnType<typeof store.getState>;
type AppDispatch = typeof store.dispatch;

export type { RootState, AppDispatch };

export { store };
