import { USER_SESSION_KEY } from '../constants/localConstants';

export const saveUserInSession = (session: object) =>
  sessionStorage.setItem(USER_SESSION_KEY, JSON.stringify(session));

export const loadUserInSession = () => {
  const json = sessionStorage.getItem(USER_SESSION_KEY);
  return JSON.parse(String(json));
};

export const clearUserInSessionStorage = () => {
  sessionStorage.removeItem(USER_SESSION_KEY);
  sessionStorage.clear();
}
