import { configureStore } from "@reduxjs/toolkit";
import {eventCacheSlice} from "./reducers/eventCache";
import {userCacheSlice} from "./reducers/userCache";

const store = configureStore({
    reducer: {
        eventCache: eventCacheSlice.reducer,
        userCache: userCacheSlice.reducer,
    },
});

type RootState = ReturnType<typeof store.getState>;
type AppDispatch = typeof store.dispatch;

export type { RootState, AppDispatch };

export { store };
