import { createSlice, PayloadAction } from "@reduxjs/toolkit";
import { RootState } from "../../index";
import {
  EventCacheProps
} from "./eventCache.types";
import {initialStateData} from "./initialState";

const initialState: EventCacheProps = initialStateData;

const eventCacheSlice = createSlice({
  name: "eventCache",
  initialState,
  reducers: {
    setEventCache: (state, action: PayloadAction<EventCacheProps>) => {
      return { ...state, ...action.payload };
    }
  }
});

const {
  setEventCache
} = eventCacheSlice.actions;

const getEventCache = (state: RootState) => state.eventCache;

export {
  eventCacheSlice,
  initialState,
  setEventCache,
  getEventCache
};
