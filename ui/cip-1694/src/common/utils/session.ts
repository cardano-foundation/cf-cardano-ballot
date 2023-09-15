const USER_SESSION_KEY = 'userInSession';

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
  return givenDate < currentDate;
};

export { saveUserInSession, getUserInSession, clearUserInSessionStorage, tokenIsExpired };
