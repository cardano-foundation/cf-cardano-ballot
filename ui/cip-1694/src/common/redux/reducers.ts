import { CLEAR_SESSION, REDUX_ERROR, SET_SESSION, SET_USER, SET_USER_IS_VERIFIED, Action } from './actionTypes';

const initialState = {
  isLoggedIn: false,
  isVerified: false,
  termsAndPrivacy: false,
  error: '',
};

const reducers = (state = initialState, action: Action) => {
  switch (action.type) {
  case SET_SESSION:
    return Object.assign({}, action.session, { isLoggedIn: true });
  case SET_USER:
    return {
      ...state,
      user: action.user,
    };
  case SET_USER_IS_VERIFIED:
    return {
      ...state,
      isVerified: action.isVerified,
    };
  case REDUX_ERROR:
    return {
      ...state,
      error: action.error,
    };
  case CLEAR_SESSION:
    return {
      ...initialState,
      termsAndPrivacy: state.termsAndPrivacy,
    };
  default:
    return state;
  }
};

export default reducers;
