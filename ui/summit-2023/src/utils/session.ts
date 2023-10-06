import { USER_SESSION_KEY } from 'common/constants/local';

const saveUserInSession = (session: { accessToken: string; expiresAt: string }) =>
  sessionStorage.setItem(USER_SESSION_KEY, JSON.stringify(session));

const getUserInSession = () => {
  const json = sessionStorage.getItem(USER_SESSION_KEY);
  return JSON.parse(json);
};

const clearUserInSessionStorage = () => {
  sessionStorage.removeItem(USER_SESSION_KEY);
  sessionStorage.clear();
};

const tokenIsExpired = (expiresAt: string) => {
  const currentDate = new Date();
  const givenDate = new Date(expiresAt);
  console.log('currentDate');
  console.log(currentDate);
  console.log('givenDate');
  console.log(givenDate);
  console.log(givenDate < currentDate)
  return givenDate < currentDate;
};

export { saveUserInSession, getUserInSession, clearUserInSessionStorage, tokenIsExpired };
