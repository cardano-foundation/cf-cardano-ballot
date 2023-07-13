import { USER_SESSION_KEY } from '../constants/localConstants';
// import { store } from '../store';
// import { CLEAR_SESSION } from '../store/actionTypes';

export const saveUserInSession = (session: any) =>
  sessionStorage.setItem(USER_SESSION_KEY, JSON.stringify(session));

export const loadUserInSession = () => {
  const json = sessionStorage.getItem(USER_SESSION_KEY);
  return JSON.parse(String(json));
};

export const clearUserInSessionStorage = () => {
  sessionStorage.removeItem(USER_SESSION_KEY);
  sessionStorage.clear();
  // store.dispatch({
  //   type: CLEAR_SESSION,
  // });
  // do const dispatch = useDispatch(); dispatch(clearSession()) on components level instead
}
