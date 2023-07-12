export const REDUX_ERROR = 'REDUX_ERROR';
export const SET_USER = 'SET_USER';
export const SET_USER_IS_VERIFIED = 'SET_USER_IS_VERIFIED';
export const CLEAR_SESSION = 'CLEAR_SESSION';
export const SET_SESSION = 'SET_SESSION';

export type Action = {
  type: typeof REDUX_ERROR | typeof SET_USER | typeof SET_USER_IS_VERIFIED | typeof CLEAR_SESSION | typeof SET_SESSION;
} & Record<number | string, unknown>;
