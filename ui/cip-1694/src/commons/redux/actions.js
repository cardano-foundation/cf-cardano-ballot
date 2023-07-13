import {REDUX_ERROR, SET_USER, SET_USER_IS_VERIFIED} from './actionTypes';

export const setUser = (user) => (dispatch) => {
  try {
    dispatch({
      type: SET_USER,
      user
    });
  } catch (error) {
    dispatch({
      type: REDUX_ERROR,
      error
    });
  }
};

export const setUserIsVerified = (isVerified) => (dispatch) => {
  try {
    dispatch({
      type: SET_USER_IS_VERIFIED,
      isVerified
    });
  } catch (error) {
    dispatch({
      type: REDUX_ERROR,
      error
    });
  }
};