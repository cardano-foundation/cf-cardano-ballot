export const USER_SESSION_KEY = 'userInSession';

export const saveUserInSession = (session: { accessToken: string; expiresAt: string }) =>
  sessionStorage.setItem(USER_SESSION_KEY, JSON.stringify(session));

export const getUserInSession = () => {
  const json = sessionStorage.getItem(USER_SESSION_KEY);
  return JSON.parse(json);
};

export const clearUserInSessionStorage = () => {
  sessionStorage.removeItem(USER_SESSION_KEY);
  sessionStorage.clear();
};

export const tokenIsExpired = (expiresAt: string) => {
  const currentDate = new Date();
  const givenDate = new Date(expiresAt);
  return givenDate < currentDate;
};
